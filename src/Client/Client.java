package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {

        Socket socket = new Socket("localhost", 4020);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Menu menu = new Menu();

        ReentrantLock lock = new ReentrantLock();
        Condition c = lock.newCondition();

        menu.setMenuOption(1);

        Reader reader = new Reader(in, lock, c, menu);

        Stub stub = new Stub(menu, lock, c, out, socket);

        stub.start();
        reader.start();

        stub.join();
        reader.join();

        in.close();
        out.close();
        System.out.println("See ya!");

        socket.close();

    }
}

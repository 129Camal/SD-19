package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(4020);
        Socket socket;
        cloudServers cs = new cloudServers();
        ReentrantLock lock = new ReentrantLock();

        try{
            while ((socket = server.accept()) != null) {
                Condition c = lock.newCondition();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                MessageInbox msg = new MessageInbox(lock,c);

                Skeleton skeleton = new Skeleton(cs, msg, in);

                Notificator notificator = new Notificator(out, c, msg);

                skeleton.start();
                notificator.start();

            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

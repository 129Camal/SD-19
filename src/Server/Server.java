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

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(4020);
        Socket socket;
        cloudServers cs = new cloudServers();
        cs.populate();
        cs.initAuctions();
        ReentrantLock lock = new ReentrantLock();

        try{
            while ((socket = server.accept()) != null) {
                Condition c = lock.newCondition();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                MessageInbox msg = new MessageInbox(lock,c);

                Skeleton skeleton = new Skeleton(cs, msg, in);

                Notificator notificator = new Notificator(out, c, lock, msg);

                skeleton.start();
                notificator.start();

            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

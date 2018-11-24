/*
Classe responsável por responder ao Cliente;
 */

package Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Condition;

public class Notificator extends Thread{
    private PrintWriter out;
    private Condition c;
    private MessageInbox msg;


    public Notificator(PrintWriter out, Condition c, MessageInbox msg) throws IOException {
        this.out = out;
        this.c = c;
        this.msg = msg;

    }

    public void run(){

            try {
                String message;
                while(true){
                    while((message = msg.getMessage())==null) c.await();
                    this.out.println(message);
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }
}

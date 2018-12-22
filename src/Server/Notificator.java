/*
Classe responsável por responder ao Cliente;
 */

package Server;


import java.io.PrintWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Notificator extends Thread{
    private PrintWriter out;
    private Condition c;
    private MessageInbox msg;
    private ReentrantLock lock;


    public Notificator(PrintWriter out, Condition c, ReentrantLock lock, MessageInbox msg) {
        this.out = out;
        this.c = c;
        this.msg = msg;
        this.lock = lock;

    }

    public void run(){
        this.lock.lock();
            try {
                String message;

                while(true){
                    while((message = msg.getMessage())==null){
                        c.await();
                    }
                    this.out.println(message);
                    System.out.println("Enviei para o cliente: " + message);
                }

            }
            catch (Exception e) {
                System.out.println("Error Noficator: " + e.getMessage());
            }finally {
                this.lock.unlock();
            }
    }
}

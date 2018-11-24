/*
Classe Midleware que permite a troca de mensagem no servidor. Entre o Skeleton e o Notificator;
 */
package Server;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageInbox {

    private ArrayList<String> messages;
    private ReentrantLock lock;
    private Condition c;

    public MessageInbox(ReentrantLock lock, Condition c){
        this.messages = new ArrayList<>();
        this.lock = lock;
        this.c = c;
    }

    public void setMessage(String msg){
        this.lock.lock();

        try{
            this.messages.add(msg);
            c.signal();
        }
        finally {
            this.lock.unlock();
        }
    }

    public String getMessage(){
        this.lock.lock();

        try{
            return this.messages.get(messages.size());
        }
        finally {
            this.lock.unlock();
        }
    }

}

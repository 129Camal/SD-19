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
    private int count;

    public MessageInbox(ReentrantLock lock, Condition c){
        this.messages = new ArrayList<>();
        this.lock = lock;
        this.c = c;
        this.count = 0;
    }

    public void setMessage(String msg){
        this.lock.lock();

        try{
            this.messages.add(msg);
            c.signal();
            //System.out.println("Adicionei a mensagem: " + msg);
        }
        finally {
            this.lock.unlock();
        }
    }

    public String getMessage(){
        this.lock.lock();

        try{
            if(this.count < messages.size())
                return this.messages.get(count++);
            else return null;
        }
        finally {
            this.lock.unlock();
        }
    }

    public ReentrantLock getLock() {
        return this.lock;
    }

    public Condition getCondition() {
        return this.c;
    }

}

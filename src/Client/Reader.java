package Client;

import java.io.BufferedReader;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Reader extends Thread{
    private BufferedReader in;
    private ReentrantLock lock;
    private Condition c;
    private Menu menu;

    public Reader(BufferedReader in, ReentrantLock lock, Condition c, Menu menu){
        this.in = in;
        this.lock = lock;
        this.c = c;
        this.menu = menu;
    }

    public void run(){

        try{
            String response;
            while((response = in.readLine())!=null){
                if(response.equals("Logged in")){
                    menu.setMenuOption(2);
                    this.lock.lock();
                        c.signal();
                    this.lock.unlock();
                } else if (response.equals("Signed in") || response.equals("Wrong Password")){
                    menu.setMenuOption(1);
                    this.lock.lock();
                        c.signal();
                    this.lock.unlock();
                }

            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

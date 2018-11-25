package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Stub extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private Menu menu;
    private ReentrantLock lock;
    private Condition c;

    public Stub(Menu menu, ReentrantLock lock, Condition c, PrintWriter out){
        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.out = out;
        this.lock = lock;
        this.menu = menu;
        this.c = c;
    }

    public void run(){
        String option;
        String aux;

        try{
            menu.setVisible();
            while((option = in.readLine())!=null){
                if(menu.getMenuOption()==1){
                    if(option.equals("1")){
                        out.println("login");
                        System.out.print("Username: ");
                        aux = in.readLine();
                        out.println(aux);
                        System.out.print("Password: ");
                        aux = in.readLine();
                        out.println(aux);
                        this.lock.lock();
                            c.await();
                        this.lock.unlock();

                    } else if (option.equals("2")) {
                        out.println("signin");
                        System.out.print("Username: ");
                        aux = in.readLine();
                        out.println(aux);
                        System.out.print("Password: ");
                        aux = in.readLine();
                        out.println(aux);
                        this.lock.lock();
                            c.await();
                        this.lock.unlock();
                    }
                    if(option.equals("1")||option.equals("2")){
                        System.out.println("\n\n\n\n\n");
                        menu.setVisible();
                    }
                }
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

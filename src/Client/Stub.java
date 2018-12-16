package Client;

import sun.jvm.hotspot.gc_implementation.parallelScavenge.PSYoungGen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Stub extends Thread {
    private BufferedReader in;
    private PrintWriter out;
    private Menu menu;
    private ReentrantLock lock;
    private Condition c;
    private Socket socket;

    public Stub(Menu menu, ReentrantLock lock, Condition c, PrintWriter out, Socket socket){
        this.in = new BufferedReader(new InputStreamReader(System.in));
        this.out = out;
        this.lock = lock;
        this.menu = menu;
        this.c = c;
        this.socket = socket;
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
                        System.out.print("Email: ");
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
                        System.out.print("Email: ");
                        aux = in.readLine();
                        out.println(aux);
                        System.out.print("Password: ");
                        aux = in.readLine();
                        out.println(aux);
                        this.lock.lock();
                            c.await();
                        this.lock.unlock();
                    }
                    else if (option.equals("0")) {
                        break;
                    }

                    if(option.equals("1") || option.equals("2")){
                        System.out.println("\n");
                        menu.setVisible();
                    }
                    continue;
                }
                if(menu.getMenuOption()==2){
                    if(option.equals("1")){
                        out.println("acqMicro");
                        this.lock.lock();
                        c.await();
                        this.lock.unlock();

                    } else if (option.equals("2")) {
                        out.println("acqLarge");
                        this.lock.lock();
                        c.await();
                        this.lock.unlock();
                    } else if (option.equals("3")) {
                        out.println("listServers");
                        this.lock.lock();
                        c.await();
                        this.lock.unlock();
                    } else if (option.equals("4")) {
                        out.println("addfounds");
                        System.out.print("Amount: ");
                        aux = in.readLine();
                        out.println(aux);

                        this.lock.lock();
                        c.await();
                        this.lock.unlock();
                    }
                    else if (option.equals("6")) {
                        out.println("personalinformation");

                        this.lock.lock();
                        c.await();
                        this.lock.unlock();
                    }
                    else if (option.equals("0")) {
                        out.println("back");
                        this.lock.lock();
                        c.await();
                        this.lock.unlock();
                    }

                    if(option.equals("1") || option.equals("2") || option.equals("3") || option.equals("4") || option.equals("6") || option.equals("0")){
                        System.out.println("\n");
                        menu.setVisible();
                    }
                    continue;
                }
                if(menu.getMenuOption()==3){
                    if(option.equals("1")){
                        out.println("endservice");
                        System.out.print("Id Booking to Terminate: ");
                        aux = in.readLine();
                        out.println(aux);

                        this.lock.lock();
                        c.await();
                        this.lock.unlock();

                    }
                    else if(option.equals("0")){
                        out.println("back");

                        this.lock.lock();
                            c.await();
                        this.lock.unlock();
                    }
                    if(option.equals("1") || option.equals("0")){
                        System.out.println("\n");
                        menu.setVisible();
                    }
                    continue;
                }

            }
            socket.shutdownOutput();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

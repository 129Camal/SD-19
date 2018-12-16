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

                //LOGIN
                if(response.equals("Logged in")){
                    menu.setMenuOption(2);
                    this.lock.lock();
                        c.signal();
                    this.lock.unlock();

                //SIGN IN
                } else if (response.equals("Signed in") || response.equals("Wrong Password") || response.equals("Not an account") || response.equals("Already in use that email!")){
                    menu.setMenuOption(1);
                    this.lock.lock();
                        c.signal();
                    this.lock.unlock();

                //SERVERS ACQUIRED WARNING
                } else if (response.equals("Micro Acquired") || response.equals("Large Acquired")){
                    System.out.println("************* MENU *******************\n");
                    System.out.println("Server Acquired!" + "\n");
                    System.out.println("**************************************");

                    menu.setMenuOption(2);
                    this.lock.lock();
                        c.signal();
                    this.lock.unlock();

                 //LIST OF SERVERS
                } else if (response.equals("List Acquired")){
                    System.out.println("************* MENU *******************\n");

                    System.out.println(in.readLine());

                    while(in.ready()){
                        System.out.println(in.readLine());
                    }
                    menu.setMenuOption(3);
                    this.lock.lock();
                    c.signal();
                    this.lock.unlock();



                    //ADD FOUNDS
                } else if (response.equals("FoundsAdded")){
                    System.out.println("************* MENU *******************\n");
                    System.out.println("Amount added to your wallet!" + "\n");
                    System.out.println("**************************************");

                    menu.setMenuOption(2);
                    this.lock.lock();
                    c.signal();
                    this.lock.unlock();
                }

                //USER INFORMATION
                else if (response.equals("userinfor")){
                    System.out.println("************* MENU *******************\n");
                    System.out.println(in.readLine()+"\n");
                    System.out.println("**************************************");

                    menu.setMenuOption(2);
                    this.lock.lock();
                    c.signal();
                    this.lock.unlock();
                }
                //End a Service
                else if (response.equals("serviceEnded")){
                    System.out.println("************* MENU *******************\n");
                    System.out.println("Service Ended! Amount removed from your wallet!");
                    System.out.println("**************************************");

                    menu.setMenuOption(2);
                    this.lock.lock();
                    c.signal();
                    this.lock.unlock();
                }
                //Back in the menu
                else if (response.equals("back")){
                    int Nmenu = menu.getMenuOption();
                    menu.setMenuOption(Nmenu - 1);
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

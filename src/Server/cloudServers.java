package Server;

import com.sun.source.tree.LambdaExpressionTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class cloudServers {

    private Map<String, User> users;    //Users at the system;
    private Map<Integer, CServerMicro> serversMicro; //ServersMicro at the system;
    private Map<Integer, CServerLarge> serversLarge;
    private Lock usersLock;
    private Lock serversMicroLock;
    private Lock serversLargeLock;
    private int idBooking;

    public cloudServers() {
        this.users = new HashMap<>();
        this.serversMicro = new HashMap<>();
        this.serversLarge = new HashMap<>();
        this.usersLock = new ReentrantLock();
        this.serversMicroLock = new ReentrantLock();
        this.serversLargeLock = new ReentrantLock();
        this.idBooking = 0;
    }

    public void populate(){

        User user = new User("camal", "129");
        users.put(user.getEmail(), user);

        for(int i = 1; i<=5; i++){
            CServerMicro m = new CServerMicro(i, "micro" + i);
            CServerLarge l = new CServerLarge(i, "large" + i);

            serversMicro.put(i, m);
            serversLarge.put(i,l);
        }
    }

    public User logIn(String email, String password) throws Exception {
        this.usersLock.lock();

        try {
            if (users.containsKey(email)) {
                if (!(users.get(email).getPassword().equals(password))) throw new Exception("Wrong Password");
            } else throw new Exception("Not an account");

            return this.users.get(email).clone();
        } finally {
            this.usersLock.unlock();
        }
    }

    public void signIn(String email, String password) throws Exception {
        this.usersLock.lock();

        try {
            if (this.users.containsKey(email)) throw new Exception("Already in use that email!");
            else {
                users.put(email, new User(email, password));
            }
        } finally {
            this.usersLock.unlock();
        }
    }

    public void assignMicro(String email){
        this.serversMicroLock.lock();

        try{
            CServerMicro m = serversMicro.values().stream()
                                                  .filter( e -> e.isAvailable())
                                                  .findAny()
                                                  .get();
            m.setAvailable(false);
            Booking booking = new Booking(idBooking, email, (CServer) m.clone());

            this.usersLock.lock();
                users.get(email).setBookings(booking);
            this.usersLock.unlock();

            this.idBooking++;

            //return (this.idBooking-1);
        }
        finally {
            this.serversMicroLock.unlock();
        }

    }

    public void assignLarge(String email){
        this.serversLargeLock.lock();

        try{
            CServerLarge l = serversLarge.values().stream()
                                                  .filter( e -> e.isAvailable())
                                                  .findAny()
                                                  .get();
            l.setAvailable(false);

            Booking booking = new Booking(this.idBooking, email, (CServer) l.clone());

            this.usersLock.lock();
                users.get(email).setBookings(booking);
            this.usersLock.unlock();

            this.idBooking++;

        }
        finally {
            this.serversLargeLock.unlock();
        }

    }

    public Set<Booking> listServers(String email){

        Set<Booking> bookings;

        this.usersLock.lock();

        try{

            bookings = users.get(email).getBookings();

            return bookings;
        }
        finally {
            this.usersLock.unlock();
        }

    }
    public User addFounds(String email, double value){

        this.usersLock.lock();

        try{
            users.get(email).addFounds(value);
            User u = users.get(email).clone();

            return u;
        }
        finally {
            this.usersLock.unlock();
        }

    }

    public User terminateServer(String email, int nBooking){

        this.usersLock.lock();

        try{
            double price = users.get(email).getBooking(nBooking).reservationTime();

            CServer server = users.get(email).getBooking(nBooking).getServer();

            if(server instanceof CServerLarge){

                this.serversLargeLock.lock();
                    CServerLarge lserver = serversLarge.get(server.getId());
                    lserver.setAvailable(true);
                this.serversLargeLock.unlock();

            } else if (server instanceof CServerMicro){

                this.serversMicroLock.lock();
                    CServerMicro mserver = serversMicro.get(server.getId());
                    mserver.setAvailable(true);
                this.serversMicroLock.unlock();
            }

            users.get(email).removeBooking(nBooking);

            users.get(email).pay(price);

            return users.get(email).clone();

        }
        finally {
            this.usersLock.unlock();
        }

    }

    
}









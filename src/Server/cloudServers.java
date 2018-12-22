package Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class cloudServers {

    private Map<String, User> users;    //Users at the system;
    private Map<Integer, CServerMicro> serversMicro; //ServersMicro at the system;
    private Map<Integer, CServerLarge> serversLarge;
    private Map<Integer, Auction> auctions;
    private ReentrantLock usersLock;
    private ReentrantLock serversMicroLock;
    private ReentrantLock serversLargeLock;
    private ReentrantLock auctionsLock;
    private int idBooking;
    private int idAuction;

    public cloudServers() {
        this.users = new HashMap<>();
        this.serversMicro = new HashMap<>();
        this.serversLarge = new HashMap<>();
        this.auctions = new HashMap<>();
        this.usersLock = new ReentrantLock();
        this.serversMicroLock = new ReentrantLock();
        this.serversLargeLock = new ReentrantLock();
        this.auctionsLock = new ReentrantLock();
        this.idBooking = 0;
        this.idAuction = 0;
    }

    public void populate(){
        int i;

        User user = new User("camal", "129");
        users.put(user.getEmail(), user);

        CServerMicro m;
        CServerLarge l;

        for(i = 1; i<=5; i++){
            m = new CServerMicro(i, "micro" + i);
            l = new CServerLarge(i, "large" + i);

            serversMicro.put(i, m);
            serversLarge.put(i,l);
        }
    }

    public void initAuctions() throws Exception {
        this.auctionsLock.lock();

        Auction m = getmicroAuction();
        Auction l = getlargeAuction();

        auctions.put(m.getId(), m);
        auctions.put(l.getId(), l);

        this.auctionsLock.unlock();

    }

    public Auction getmicroAuction() throws Exception{
        this.serversMicroLock.lock();

        try {
            CServerMicro m = serversMicro.values().stream()
                    .filter(e -> e.isAvailable())
                    .findAny()
                    .get();

            if(m == null) throw new Exception("No Micro Server Available for Auction!");

            m.setAvailable(false);
            Auction auction = new Auction(idAuction, (CServer) m.clone());
            this.idAuction++;

            return auction;

        } finally {
            this.serversMicroLock.unlock();
        }
    }

    public Auction getlargeAuction() throws Exception {
        this.serversLargeLock.lock();

        try {
            CServerLarge l = serversLarge.values().stream()
                    .filter(e -> e.isAvailable())
                    .findAny()
                    .get();

            if(l == null) throw new Exception("No Large Server Available for Auction!");

            l.setAvailable(false);
            Auction auction = new Auction(idAuction, (CServer) l.clone());
            this.idAuction++;

            return auction;

        } finally {
            this.serversLargeLock.unlock();
        }
    }

    public Set<Auction> listAuctionMicro(){
        this.auctionsLock.lock();
        Set<Auction> microAuctions;

        try{
             microAuctions = auctions.values().stream()
                    .filter(e -> e.getServer() instanceof CServerMicro)
                    .collect(Collectors.toSet());

            return microAuctions;
        } finally {
            this.auctionsLock.unlock();
        }
    }

    public Set<Auction> listAuctionLarge(){
        this.auctionsLock.lock();
        Set<Auction> largeAuctions;

        try{
            largeAuctions = auctions.values().stream()
                    .filter(e -> e.getServer() instanceof CServerLarge)
                    .collect(Collectors.toSet());

            return largeAuctions;
        } finally {
            this.auctionsLock.unlock();
        }
    }

    public void bid(String email, int idAuction, double value) throws Exception{
        this.auctionsLock.lock();

        try{

            auctions.get(idAuction).bid(email, value);

        } finally {

            this.auctionsLock.unlock();

        }
    }

    public User endAuction(String email, int idAuction){
        this.auctionsLock.lock();

        try{
            Auction auc = auctions.get(idAuction);

            Booking booking = new Booking(idBooking, email, auc.getServer());
            idBooking++;
            auctions.remove(idAuction);

            this.usersLock.lock();
                users.get(email).setBookings(booking);
            this.usersLock.unlock();

            return users.get(email).clone();

        } finally {
            this.auctionsLock.unlock();
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

    public String assignMicro(String email) throws Exception {
        this.serversMicroLock.lock();

        String retorno;
        CServerMicro m;

        try{
            long l = serversMicro.values().stream()
                    .filter( e -> e.isAvailable())
                    .count();

            if(l > 0) {
                m = serversMicro.values().stream()
                        .filter(e -> e.isAvailable())
                        .findAny()
                        .get();
                retorno = "Micro Acquired";
            }
            else {
                m = (CServerMicro) getServerMicroFromAuction();
                retorno = "Auction Micro Canceled!";
            }

            m.setAvailable(false);
            Booking booking = new Booking(idBooking, email, (CServer) m.clone());

            this.usersLock.lock();
                users.get(email).setBookings(booking);
            this.usersLock.unlock();

            this.idBooking++;

            return retorno;

        }
        finally {
            this.serversMicroLock.unlock();
        }

    }

    public CServer getServerMicroFromAuction() throws Exception {
            this.auctionsLock.lock();
            CServer server;

            try {

                long aucount = auctions.values()
                        .stream()
                        .filter(e -> e.getServer() instanceof CServerMicro)
                        .count();

                if(aucount > 0){
                    Auction auc = auctions.values()
                            .stream()
                            .filter(e -> e.getServer() instanceof CServerMicro)
                            .findAny()
                            .get();
                    server = auc.getServer();
                    auctions.remove(auc.getId());

                } else {
                    throw new Exception("Server Unavailable");
                }

                return server;

            } finally {
                this.auctionsLock.unlock();
            }


    }

    public String assignLarge(String email) throws Exception {
        this.serversLargeLock.lock();
        CServerLarge l;
        String retorno;

        try{
            long count = serversLarge.values().stream()
                    .filter( e -> e.isAvailable())
                    .count();

            if(count > 0) {
                 l = serversLarge.values().stream()
                                                  .filter( e -> e.isAvailable())
                                                  .findAny()
                                                  .get();
                 retorno = "Large Acquired";
            } else{
                l = (CServerLarge) getServerLargeFromAuction();
                retorno = "Auction Large Canceled!";
            }

            l.setAvailable(false);

            Booking booking = new Booking(this.idBooking, email, (CServer) l.clone());

            this.usersLock.lock();
                users.get(email).setBookings(booking);
            this.usersLock.unlock();

            this.idBooking++;

            return retorno;
        }
        finally {
            this.serversLargeLock.unlock();
        }

    }

    public CServer getServerLargeFromAuction() throws Exception {
        this.auctionsLock.lock();
        CServer server;

        try {
            long aucount = auctions.values()
                    .stream()
                    .filter(e -> e.getServer() instanceof CServerLarge)
                    .count();

            if(aucount > 0){
                Auction auc = auctions.values()
                        .stream()
                        .filter(e -> e.getServer() instanceof CServerLarge)
                        .findAny()
                        .get();

                server = auc.getServer();
                auctions.remove(auc.getId());


            } else {
                throw new Exception("Server Unavailable");
            }

            return server;

        } finally {
            this.auctionsLock.unlock();
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









package Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;


public class cloudServers {

    private Map<String, User> users;    //Users at the system;
    private Map<Integer, CServerMicro> serversMicro; //ServersMicro at the system;
    private Map<Integer, CServerLarge> serversLarge;
    private Map<Integer, Auction> auctions;
    private Map<String, MessageInbox> userMessages;
    private ReentrantLock messagesLock;
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
        this.userMessages = new HashMap<>();
        this.messagesLock = new ReentrantLock();
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

    /*
    public void initAuctions() throws Exception {
        this.auctionsLock.lock();

        Auction m = getmicroAuction();
        Auction l = getlargeAuction();

        auctions.put(m.getId(), m);
        auctions.put(l.getId(), l);

        this.auctionsLock.unlock();

    }
    */

    public void manageMicroAuctions(){

        while(true) {

            this.auctionsLock.lock();
            long aucount = auctions.values()
                    .stream()
                    .filter(e -> e.getServer() instanceof CServerMicro)
                    .count();
            this.auctionsLock.unlock();

            this.serversMicroLock.lock();
            long numberMicro = serversMicro.values()
                    .stream()
                    .filter(e -> e.isAvailable())
                    .count();
            this.serversMicroLock.unlock();

            if(aucount == 0 && numberMicro > 0){
                try {
                    //Criar Leilão
                    this.auctionsLock.lock();
                    Auction auc = getmicroAuction();
                    auctions.put(auc.getId(), auc);
                    this.auctionsLock.unlock();

                    //Esperar 1 min para o leilão decorrer;
                    TimeUnit.MINUTES.sleep(1);

                    if(!auctions.containsKey(auc.getId())){
                        continue;

                    } else{
                      endAuction(auc.getId());
                    }

                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    public void manageLargeAuctions(){

        while(true) {

            this.auctionsLock.lock();
            long aucount = auctions.values()
                    .stream()
                    .filter(e -> e.getServer() instanceof CServerLarge)
                    .count();
            this.auctionsLock.unlock();

            this.serversLargeLock.lock();
            long numberlarge = serversLarge.values()
                    .stream()
                    .filter(e -> e.isAvailable())
                    .count();
            this.serversLargeLock.unlock();

            if(aucount == 0 && numberlarge > 0){
                try {
                    //Criar Leilão
                    this.auctionsLock.lock();
                    Auction auc = getlargeAuction();
                    auctions.put(auc.getId(), auc);
                    this.auctionsLock.unlock();

                    //Esperar 1 min para o leilão decorrer;
                    TimeUnit.MINUTES.sleep(1);

                    if(!auctions.containsKey(auc.getId())){
                        continue;

                    } else{
                        endAuction(auc.getId());
                    }

                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    private void endAuction(int idAuction){
        this.auctionsLock.lock();

        try{
            Auction auc = auctions.get(idAuction);
            String email = auc.getBestBid().getBuyer();
            Double price = auc.getBestBid().getBidValue();

            auc.getServer().setPrice(price);

            auctions.remove(idAuction);

            if(email!=null) {
                Booking booking = new Booking(idBooking, email, auc.getServer());
                idBooking++;

                this.usersLock.lock();
                    users.get(email).setBookings(booking);
                this.usersLock.unlock();
            }else{
                if(auc.getServer() instanceof CServerMicro){
                    this.serversMicroLock.lock();
                        serversMicro.get(auc.getServer().getId()).setAvailable(true);
                    this.serversMicroLock.unlock();
                }
                if(auc.getServer() instanceof CServerLarge){
                    this.serversLargeLock.lock();
                        serversLarge.get(auc.getServer().getId()).setAvailable(true);
                    this.serversLargeLock.unlock();
                }

            }
        } finally {
            this.auctionsLock.unlock();
        }
    }

    public Auction getmicroAuction() throws Exception{
        this.serversMicroLock.lock();
        Auction auction;

        try {

            CServerMicro m = serversMicro.values().stream()
                    .filter(e -> e.isAvailable())
                    .findAny()
                    .get();

            m.setAvailable(false);
            auction = new Auction(idAuction, (CServer) m.clone());
            this.idAuction++;

            return auction;

        } finally {
            this.serversMicroLock.unlock();
        }
    }

    public Auction getlargeAuction() throws Exception {
        this.serversLargeLock.lock();
        Auction auction;

        try {
            CServerLarge m = serversLarge.values().stream()
                    .filter(e -> e.isAvailable())
                    .findAny()
                    .get();

            m.setAvailable(false);
            auction = new Auction(idAuction, (CServer) m.clone());
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





    public User logIn(String email, String password, MessageInbox m) throws Exception {
        this.usersLock.lock();

        try {
            if (users.containsKey(email)) {
                if (!(users.get(email).getPassword().equals(password))) throw new Exception("Wrong Password");
            } else throw new Exception("Not an account");

        } finally {
            this.usersLock.unlock();
        }

        this.messagesLock.lock();

        try{
            if(this.userMessages.containsKey(email)){
                this.userMessages.remove(email);
                this.userMessages.put(email, m);
            } else{
                this.userMessages.put(email, m);
            }
        } finally {
            this.messagesLock.unlock();
        }
        this.usersLock.lock();

        try {
            return this.users.get(email).clone();
        } finally {
            this.usersLock.unlock();
        }

    }

    public void signIn(String email, String password, MessageInbox m) throws Exception {
        this.usersLock.lock();

        try {
            if (this.users.containsKey(email)) throw new Exception("Already in use that email!");
            else {
                users.put(email, new User(email, password));
            }
        } finally {
            this.usersLock.unlock();
        }

        this.messagesLock.lock();

        try{

            this.userMessages.put(email, m);

        } finally {
            this.messagesLock.unlock();
        }
    }

    private void notifyUsers(String s){
        this.usersLock.lock();

        try{
            userMessages.forEach((email, message) -> {
                message.setMessage(s);
            });

        }
        finally {
            this.usersLock.unlock();
        }
    }

    public void assignMicro(String email) throws Exception {
        this.serversMicroLock.lock();

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
            }
            else {
                m = (CServerMicro) getServerMicroFromAuction();
                notifyUsers("Auction Micro Canceled!");
            }

            m.setAvailable(false);
            Booking booking = new Booking(idBooking, email, (CServer) m.clone());

            this.usersLock.lock();
                users.get(email).setBookings(booking);
            this.usersLock.unlock();

            this.idBooking++;

        }
        finally {
            this.serversMicroLock.unlock();
        }

    }

    private CServer getServerMicroFromAuction() throws Exception {
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

    public void assignLarge(String email) throws Exception {
        this.serversLargeLock.lock();
        CServerLarge l;

        try{
            long count = serversLarge.values().stream()
                    .filter( e -> e.isAvailable())
                    .count();

            if(count > 0) {
                 l = serversLarge.values().stream()
                                                  .filter( e -> e.isAvailable())
                                                  .findAny()
                                                  .get();
            } else{
                l = (CServerLarge) getServerLargeFromAuction();
                notifyUsers("Auction Large Canceled!");
            }

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

    private CServer getServerLargeFromAuction() throws Exception {
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









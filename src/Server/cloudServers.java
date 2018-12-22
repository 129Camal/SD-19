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

    /** Populate The Structures
     * @return void
     * */
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

    /** Manage the Auctions for Micro Servers
     * @return void
     * */
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

    /** Manage the Auctions for Large Servers
     * @return void
     * */
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


                    //Terminar com o leilão
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

    /** End Auction
     * @return void
     * @param idAuction is the id of the auction to end
     * */
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

    /** Get a Micro Server and create a Auction
     * @return Auction
     * */
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

    /** Get a Large Server and create a Auction
     * @return Auction
     * */
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

    /** Get a list of Auctions for Micro Servers that are live!
     * @return Set of Auctions
     * */
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

    /** Get a list of Auctions for Large Servers that are live!
     * @return Set of Auctions
     * */
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

    /** Do a Bid for a certain live Auction
     * @return void
     * @param email is the email of the user
     * @param idAuction is the id of the Auction
     * @param value is the value of the bid
     * */
    public void bid(String email, int idAuction, double value) throws Exception{
        this.auctionsLock.lock();

        try{
            if(auctions.containsKey(idAuction)){
                auctions.get(idAuction).bid(email, value);
            } else{
                throw new Exception("Wrong Auction!");
            }


        } finally {

            this.auctionsLock.unlock();

        }
    }

    /** Log In
     * @return User who just logged in
     * */
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

    /** Sign In
     * @return void
     * */
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

    /** Notify all Users to certain event
     * @return void
     * @param s is the message to send
     * */
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

    /** Assign a Micro Server to a certain User
     * @return void
     * @param email is the email of the User who wants the server
     * */
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
                m.setAvailable(false);
            }

            else {
                Auction auc = getServerMicroFromAuction();
                m = (CServerMicro) auc.getServer();
                notifyUsers("Auction Micro Canceled with ID: " + auc.getId() + "!");
            }

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

    /** Get a Auction that contains a micro server from a live auction!
     * @return Auction
     * */
    private Auction getServerMicroFromAuction() throws Exception {
            this.auctionsLock.lock();
            Auction auc;

            try {

                long aucount = auctions.values()
                        .stream()
                        .filter(e -> e.getServer() instanceof CServerMicro)
                        .count();

                if(aucount > 0){
                    auc = auctions.values()
                            .stream()
                            .filter(e -> e.getServer() instanceof CServerMicro)
                            .findAny()
                            .get();

                    auctions.remove(auc.getId());
                    return auc;

                } else {
                    throw new Exception("Server Unavailable");
                }
            } finally {
                this.auctionsLock.unlock();
            }


    }

    /** Assign a Large Server to a certain User
     * @return void
     * @param email is the email of the User who wants the server
     * */
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
                l.setAvailable(false);
            } else{
                Auction auc = getServerLargeFromAuction();
                l = (CServerLarge) auc.getServer();
                notifyUsers("Auction Large Canceled with ID: " + auc.getId() + "!");
            }

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

    /** Get a Auction that contains a large server from a live auction!
     * @return Auction
     * */
    private Auction getServerLargeFromAuction() throws Exception {
        this.auctionsLock.lock();
        Auction auc;

        try {
            long aucount = auctions.values()
                    .stream()
                    .filter(e -> e.getServer() instanceof CServerLarge)
                    .count();

            if(aucount > 0){
                auc = auctions.values()
                        .stream()
                        .filter(e -> e.getServer() instanceof CServerLarge)
                        .findAny()
                        .get();

                auctions.remove(auc.getId());
                return auc;

            } else {
                throw new Exception("Server Unavailable");
            }

        } finally {
            this.auctionsLock.unlock();
        }


    }

    /** Get all the reserved servers of an user
     * @return Set of Bookings
     * @param email is the email of the user
     * */
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

    /** Add founds to the wallet of a certain user
     * @return User that the founds were added
     * @param email is the email of the user
     * @param value is the amount to add to the wallet
     * */
    public void addFounds(String email, double value){

        this.usersLock.lock();

        try{
            users.get(email).addFounds(value);
            User u = users.get(email).clone();
        }
        finally {
            this.usersLock.unlock();
        }

    }

    /** End a reservation
     * @return User
     * @param email is the email of the user
     * @param nBooking is the id of the reservation to end
     * */
    public void terminateServer(String email, int nBooking) throws Exception {

        this.usersLock.lock();

        try{

            if(users.get(email).getBooking(nBooking) != null) {
                double price = users.get(email).getBooking(nBooking).reservationTime();

                CServer server = users.get(email).getBooking(nBooking).getServer();

                if (server instanceof CServerLarge) {

                    this.serversLargeLock.lock();
                    CServerLarge lserver = serversLarge.get(server.getId());
                    lserver.setAvailable(true);
                    this.serversLargeLock.unlock();

                } else if (server instanceof CServerMicro) {

                    this.serversMicroLock.lock();
                    CServerMicro mserver = serversMicro.get(server.getId());
                    mserver.setAvailable(true);
                    this.serversMicroLock.unlock();
                }

                users.get(email).removeBooking(nBooking);

                users.get(email).pay(price);
            } else {
                throw new Exception("Invalid Reservation");
            }
        }
        finally {
            this.usersLock.unlock();
        }

    }

    /** Get the personal data from a user
     * @return User
     * @param email is the email of the user
     * */
    public User getPersonalData(String email){
        this.usersLock.lock();

        try{

            User user = users.get(email).clone();
            return user;

        } finally {
            this.usersLock.unlock();
        }
    }

    
}









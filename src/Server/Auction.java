package Server;

import java.util.ArrayList;

public class Auction {
    private int id;
    private CServer server;
    private Bid bestBid;

    public Auction(int id, CServer server) {
        this.id = id;
        this.server = server;
        this.bestBid = new Bid(null, 0);
    }

    synchronized public void bid(String user, double price) throws Exception {
        if (this.bestBid.getBidValue() > price)
            throw new Exception("Highest Value Bidded!");

        bestBid = new Bid(user, price);
    }

    public int getId(){
        return this.id;
    }

    public CServer getServer(){
        return this.server;
    }

    public Bid getBestBid(){
        return this.bestBid;
    }

    @Override
    public String toString() {
        return "Auction " + id +
                ", server=" + server.getName() +
                ", bestBid= {" + bestBid.toString() + "}";
    }
}

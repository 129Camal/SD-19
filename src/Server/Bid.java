package Server;

public class Bid {
    private final String buyer;
    private final double bidvalue;

    public Bid(String buyer, double bidvalue) {
        this.buyer = buyer;
        this.bidvalue = bidvalue;
    }

    public String getBuyer(){
        return this.buyer;
    }

    public double getBidValue(){
        return this.bidvalue;
    }

    public Object clone(){
        return new Bid(this.buyer, this.bidvalue);
    }

    @Override
    public String toString() {
        return "Buyer = " + buyer +
                ", Value = " + bidvalue;
    }
}

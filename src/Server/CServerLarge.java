package Server;

public class CServerLarge extends CServer{
    private double price;

    public CServerLarge(int id, String name) {
        super(id, name);
        this.price = 2.5;
    }

    public int getId() {
        return super.getId();
    }

    public String getName() { return super.getName(); }

    public double getPrice() { return this.price; }

    public boolean isAvailable() { return super.isAvailable(); }

    public void setAvailable(boolean available){ super.setAvailable(available);}

    public Object clone(){
        return new CServerLarge(this.getId(), this.getName());
    }
}


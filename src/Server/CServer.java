package Server;

public class CServer {

    private int id;
    private String name;
    private double price;
    private boolean available;

    public CServer(int id, String name){
        this.id = id;
        this.name = name;
        this.available = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available){
        this.available = available;
    }
}

package Server;

public class CServerMicro extends CServer{
    private double price;

    public CServerMicro(int id, String name) {
        super(id, name);
        this.price = 0.99;
    }

    public int getId() {
        return super.getId();
    }

    public String getName() { return super.getName();
    }

    public double getPrice() { return this.price;
    }

    public boolean isAvailable() { return super.isAvailable();
    }

    public void setAvailable(boolean available){ super.setAvailable(available);
    }

    public Object clone(){
        return new CServerMicro(this.getId(), this.getName());
    }
}

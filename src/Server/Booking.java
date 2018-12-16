package Server;

import java.time.Duration;
import java.time.LocalDateTime;

public class Booking {

    private int idBooking;
    private String user;
    private CServer server;
    private LocalDateTime inicialDate;
    private LocalDateTime endDate;

    public Booking(int idBooking, String user, CServer server) {
        this.idBooking = idBooking;
        this.user = user;
        this.server = server;
        this.inicialDate = LocalDateTime.now();

    }

    public int getIdBooking() {
        return idBooking;
    }

    public String getUser() {
        return user;
    }

    public CServer getServer() {
        return this.server;
    }


    public int getidServer() {
        return server.getId();
    }

    public double reservationTime(){
        this.endDate = LocalDateTime.now();

        long diffInSeconds = Duration.between(this.inicialDate, this.endDate).getSeconds();

        double hour = ((diffInSeconds / 60.0) / 60.0);

        double price = hour * server.getPrice();

        return price;

    }

    @Override
    public String toString() {
        return  "Nº Booking = " + idBooking +
                " , Server Name = " + server.getName() +
                " , Initial Hour = " + inicialDate + ";";
    }

    public Booking clone(){
        return new Booking(this.idBooking, this.user, this.server);
    }
}
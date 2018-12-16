/*
Classe referente a um utilizador da aplicação;s
 */
package Server;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;


public class User {
    private String email;
    private String password;
    private double wallet;
    private int nBookings;
    private HashMap<Integer, Booking> bookings;


    public User(String email, String password){
        this.email = email;
        this.password = password;
        this.wallet = 0;
        this.nBookings = 0;
        this.bookings = new HashMap<>();
    }

    public User(User a){
        this.email = a.getEmail();
        this.password = a.getPassword();
        this.wallet = a.getWallet();
        this.nBookings = a.getnBookings();
    }

    public int getnBookings(){
        return nBookings;
    }

    public Booking getBooking(int idBooking) {
        return this.bookings.get(idBooking);
    }

    public Set<Booking> getBookings(){
        Set<Booking> result = new HashSet<>(this.bookings.values());

        return result;
    }

    public void setBookings(Booking booking) {
        this.bookings.put(booking.getIdBooking(), booking);
        this.nBookings++;
    }

    public void removeBooking(int idBooking){
        this.bookings.remove(idBooking);
    }

    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }

    public double getWallet() {
        return wallet;
    }

    public void addFounds(double wallet) {
        this.wallet += wallet;
    }

    public void pay(double value){
        this.wallet -= value;
    }

    @Override
    public String toString() {
        return "Email = " + email + ", Password = " + password +
                ", Wallet = " + wallet +
                ", Bookings = " + nBookings;
    }

    public User clone(){
        return new User(this);
    }

}

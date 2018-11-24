/*
Classe referente a um utilizador da aplicação;s
 */
package Server;

public class User {
    private String username;
    private String password;
    private double wallet;

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.wallet = 0;
    }


    public String getUsername() {
        return username;
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
}

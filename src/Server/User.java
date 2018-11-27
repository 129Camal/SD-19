/*
Classe referente a um utilizador da aplicação;s
 */
package Server;

public class User {
    private String email;
    private String password;
    private double wallet;

    public User(String username, String password){
        this.email = username;
        this.password = password;
        this.wallet = 0;
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
}

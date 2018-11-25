package Server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class cloudServers {

    private Map<String, User> users;    //Users at the system;
    private Map<String, CServer> servers;   //Servers at the system;
    private Lock usersLock;
    private Lock serversLock;

    public cloudServers(){
        this.users = new HashMap<>();
        this.servers = new HashMap<>();
        this.usersLock = new ReentrantLock();
        this.serversLock = new ReentrantLock();
    }

    public User logIn(String username, String password) throws Exception {
        this.usersLock.lock();

        try{
            if(users.containsKey(username)){
                if(users.get(username).getPassword().equals(password)) throw new Exception("Wrong Password!");
            }
            else throw new Exception("Not an account");

            return this.users.get(username);
        }
        finally {
            this.usersLock.unlock();
        }
    }

    public void signIn(String username, String password) throws Exception {
        this.usersLock.lock();

        try{
            if(this.users.containsKey(username)) throw new Exception("Already in use that username!");
            else{
                users.put(username, new User(username, password));
                System.out.println("User " + username + " com pass: " + password + " adicionado com sucesso!");
            }
        }
        finally {
            this.usersLock.unlock();
        }
    }
}

/*
Classe referente ao Skeleton, responsável por ouvir os pedidos do cliente e executar as acções.
 */

package Server;

import java.io.BufferedReader;
import java.io.IOException;

public class Skeleton extends Thread{

    private User user;
    private BufferedReader in;
    private cloudServers cs;
    private MessageInbox msg;

    public Skeleton(cloudServers cs, MessageInbox msg, BufferedReader in) throws IOException {
        this.cs = cs;
        this.in = in;
        this.msg = msg;
        this.user = null;
    }

    public void run() {
        try {
            String order;
            while ((order = in.readLine()) != null) {

                String username, password;

                //caso o pedido recebido seja login;
                if (order.equals("login")) {

                    username = in.readLine();
                    password = in.readLine();
                    try {
                        this.user = cs.logIn(username, password);
                        msg.setMessage("Logged In");
                    } catch (Exception e) {
                        msg.setMessage(e.getMessage());
                    }
                }

                //caso o pedido recebido seja signin;
                else if (order.equals("signin")) {
                    username = in.readLine();
                    password = in.readLine();

                    try{
                        cs.signIn(username, password);
                        msg.setMessage("Signed In");
                    } catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

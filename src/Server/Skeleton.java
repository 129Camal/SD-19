/*
Classe referente ao Skeleton, responsável por ouvir os pedidos do cliente e executar as acções.
 */

package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

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

                String email, password, value;

                //caso o pedido recebido seja login;
                if (order.equals("login")) {

                    email = in.readLine();
                    password = in.readLine();
                    try {
                        this.user = cs.logIn(email, password);
                        msg.setMessage("Logged in");
                    } catch (Exception e) {
                        msg.setMessage(e.getMessage());
                    }
                }

                //caso o pedido recebido seja signin;
                else if (order.equals("signin")) {
                    email = in.readLine();
                    password = in.readLine();

                    try{
                        cs.signIn(email, password);
                        msg.setMessage("Signed in");
                    } catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }

                //caso o pedido recebido seja para adquirir server micro
                else if(order.equals("acqMicro")){

                    try {
                        cs.assignMicro(this.user.getEmail());
                        msg.setMessage("Micro Acquired");
                    } catch (Exception e) {
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido recebido seja para adquirir server large
                else if(order.equals("acqLarge")){
                   try{
                       cs.assignLarge(this.user.getEmail());
                       msg.setMessage("Large Acquired");
                   }
                   catch (Exception e){
                       msg.setMessage(e.getMessage());
                   }
                }
                //caso o pedido recebido seja para mostrar todos os servers alugados
                else if(order.equals("listServers")){
                    try{
                        Set<Booking> serversAcquired = cs.listServers(this.user.getEmail());

                        StringBuilder sb = new StringBuilder();

                        msg.setMessage("List Acquired");

                        for(Booking book: serversAcquired) {
                            sb.append(book.toString());
                            sb.append("\n");
                        }

                        msg.setMessage(sb.toString());
                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido seja para adicionar fundos
                else if(order.equals("addfounds")){
                    try{
                        value = in.readLine();

                        this.user = cs.addFounds(this.user.getEmail(), Double.parseDouble(value));

                        msg.setMessage("FoundsAdded");

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido seja para mostrar a informação pessoal
                else if(order.equals("personalinformation")){
                    try{

                        value = this.user.toString();
                        msg.setMessage("userinfor");
                        msg.setMessage(value);

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido seja para acabar com um aluguer de um servidor
                else if(order.equals("endservice")){
                    try{

                        value = in.readLine();

                        this.user = cs.terminateServer(this.user.getEmail(), Integer.parseInt(value));


                        msg.setMessage("serviceEnded");

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido seja para voltar atrás no menu!
                else if(order.equals("back")){
                    try{
                        msg.setMessage("back");

                    }
                    catch (Exception e){
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

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

    public Skeleton(cloudServers cs, MessageInbox msg, BufferedReader in)  {
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
                        this.user = cs.logIn(email, password, msg);
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
                        cs.signIn(email, password, msg);
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

                        cs.addFounds(this.user.getEmail(), Double.parseDouble(value));

                        msg.setMessage("FoundsAdded");

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido seja para mostrar a informação pessoal
                else if(order.equals("personalinformation")){
                    try{

                        this.user = cs.getPersonalData(this.user.getEmail());
                        msg.setMessage("userinfor");
                        msg.setMessage(this.user.toString());

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //caso o pedido seja para acabar com um aluguer de um servidor
                else if(order.equals("endservice")){
                    try{

                        value = in.readLine();

                        cs.terminateServer(this.user.getEmail(), Integer.parseInt(value));


                        msg.setMessage("serviceEnded");

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                //Listar os servidores micro em leilão
                else if(order.equals("listAuctionsMicro")){
                    try{

                        Set<Auction> microAuction = cs.listAuctionMicro();

                        StringBuilder sb = new StringBuilder();

                        msg.setMessage("List Auction Micro");

                        for(Auction auc: microAuction) {
                            sb.append(auc.toString());
                            sb.append("\n");
                        }

                        msg.setMessage(sb.toString());

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }

                //Listar os servidores large em Leilão
                else if(order.equals("listAuctionsLarge")){
                    try{

                        Set<Auction> largeAuction = cs.listAuctionLarge();

                        StringBuilder sb = new StringBuilder();

                        msg.setMessage("List Auction Large");

                        for(Auction auc: largeAuction) {
                            sb.append(auc.toString());
                            sb.append("\n");
                        }

                        msg.setMessage(sb.toString());

                    }
                    catch (Exception e){
                        msg.setMessage(e.getMessage());
                    }
                }
                else if(order.equals("bid")){
                    try{

                        String auction = in.readLine();
                        String bid = in.readLine();

                        cs.bid(this.user.getEmail(), Integer.parseInt(auction), Double.parseDouble(bid));


                        msg.setMessage("Bidded");

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

package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    public static void main(String[] args){
        try{
            Socket socket = new Socket("localhost", 4020);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (Exception e){

        }
    }
}

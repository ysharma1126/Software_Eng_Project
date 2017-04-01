package server;

import java.io.*;
import java.net.*;

import message.LoginMessage;
import message.LoginResponse;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        /*
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }
        
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        */
      
        String hostName = "199.98.20.114";
        int portNumber = 8080;
        System.out.println("Yash it worked");
        
        try (
            Socket gameSocket = new Socket(hostName, portNumber);
        	ObjectOutputStream serverOutput = new ObjectOutputStream(gameSocket.getOutputStream());
        	ObjectInputStream serverInput = new ObjectInputStream(gameSocket.getInputStream());
            BufferedReader in = new BufferedReader(
                new InputStreamReader(gameSocket.getInputStream()));
        ) {
            LoginMessage login_info = new LoginMessage("Shalin","123");
            login_info.send(serverOutput);
            LoginResponse login_response = (LoginResponse) serverInput.readObject();
            System.out.println("Authentication Response: " + login_response.is_valid);
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } catch (ClassNotFoundException e){
        	System.err.println(e.getMessage());
        	System.exit(1);
        }
    }
}
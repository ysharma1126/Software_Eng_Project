package Backend;

import java.io.*;
import java.net.*;
import org.json.simple.JSONObject;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            Socket gameSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(gameSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(gameSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            
            JSONObject login_info = new JSONObject();
            login_info.put("username", "Shalin");
            login_info.put("password", "123");
            
            out.println(login_info.toJSONString());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}
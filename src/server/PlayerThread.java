package server;
import java.io.*;
import java.net.*;
import gamelogic.*;
import ui.LoginMessage;

/**
 * A thread for a single player.
 * @author Shalin
 *
 */

public class PlayerThread implements Runnable {
    private Socket socket = null;
    public Player player = null;
    private ObjectInputStream clientInput = null;
    
    public PlayerThread(Socket socket) throws IOException{
        this.socket = socket;
        clientInput = new ObjectInputStream(socket.getInputStream());
    }

    public void run() {
    	
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {

            this.authenticate();
            this.terminate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Boolean authenticate() throws IOException{
    	Boolean authenticate_status = false;
    	String username, password;
    	LoginMessage inputObject;
    	
    	try {
	        inputObject = (LoginMessage)clientInput.readObject();
	        username = inputObject.username;
	        password = inputObject.password;
	    	System.out.println("Recieved from client:" + username + password);
    	} catch (ClassNotFoundException e) {
        	e.printStackTrace();
        }
    	return authenticate_status;
    }
    
    public void terminate() throws IOException{
		socket.close();
    }
}
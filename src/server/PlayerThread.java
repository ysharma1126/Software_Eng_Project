package server;
import java.io.*;
import java.net.*;
import gamelogic.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/**
 * A thread for a single player.
 * @author Shalin
 *
 */

public class PlayerThread implements Runnable {
    private Socket socket = null;
    public Player player = null;
    
    public PlayerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
    	
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
        	String inputLine;
            inputLine = in.readLine();
            if (inputLine == null)
            	this.terminate();
            System.out.println("Recieved from client:" + inputLine);
            this.authenticate(inputLine);
            this.terminate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Boolean authenticate(String login_info_string) throws IOException{
    	Boolean authenticate_status = false;
    	String username, password;
    	try{
	    	JSONParser jparser = new JSONParser();
	    	JSONObject login_info = (JSONObject) jparser.parse(login_info_string);
	    	username = (String) login_info.get("username");
	    	password = (String) login_info.get("password");
	    	if (username == null || password == null){
	    		System.err.println("Username or password missing from client");
	    		this.terminate();
	    	}
	    	// TODO: Connect to DB to verify username/password
    	} catch(ParseException e){
    		System.err.println("Error parsing JSON object: " + e.getMessage());
    		this.terminate();
    	}
    	return authenticate_status;
    }
    
    public void terminate() throws IOException{
		socket.close();
    }
}
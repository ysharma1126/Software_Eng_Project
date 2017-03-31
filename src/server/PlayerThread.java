package server;
import java.io.*;
import java.net.*;
import gamelogic.*;
import message.CreateGame;
import message.CreateGameResponse;
import message.JoinGame;
import message.JoinGameResponse;
import message.LoginMessage;
import message.LoginResponse;
import message.SetSelectResponse;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;

/**
 * A thread for a single player/client.
 * Handles communication with the client for things such as
 * authentication, stat retrieval, etc
 * @author Shalin
 *
 */

public class PlayerThread implements Runnable {
    private Socket socket = null;
    public Player player = null;
    private ObjectInputStream clientInput = null;
    private ObjectOutputStream clientOutput = null;
    
    /**
     * Initializes the PlayerThread. Keeps track of the given socket and
     * creates the Object Stream's to communicate with the Client
     * @param	socket	The Socket object for the client
     * @author Shalin
     */
    public PlayerThread(Socket socket) throws IOException{
        this.socket = socket;
        clientInput = new ObjectInputStream(socket.getInputStream());
        clientOutput = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * Obtained from the Runnable interface. Is called from Thread.start().
	 * It is essentially the main method for the thread, handling
	 * authentication and subsequently requests to check stats, join
	 * a game, logout, etc.
     * @author Shalin
     */
    public void run() {
    	
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
        	// try authenticating 3 times
        	for(int i=0; i<3; i++){
        		if (this.authenticate()){
        			LoginResponse lr = new LoginResponse(true);
        			lr.send(clientOutput);
        			Server.connected_playerInput.put(player, clientInput);
        			Server.connected_playerOutput.put(player, clientOutput);
        			break;
        		}
        	}
            this.terminate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Requests for checking stats, joining a game, logging out, etc
        while (true) {
        	Object obj;
        	try {
				obj = (Object) clientInput.readObject();
				//if (obj instanceof StatsRequest) {
				//	Stats stat = new Stats(this.getStats());
				//}
				if (obj instanceof CreateGame) {
					CreateGame resp = (CreateGame) obj;
					GameThread gt = new GameThread(player, socket, Server.gamesize);
	    			Thread t = new Thread(gt);
	    			t.start();
	    			
	    			Server.connected_games.put(Server.gamesize, gt);
	    			
	    			CreateGameResponse cgr = new CreateGameResponse(player, Server.gamesize);
	    			for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    				cgr.send(value);
	    			}
					
	    			Server.gamesize++;
				}
				else if (obj instanceof JoinGame) {
					JoinGame resp = (JoinGame) obj;
					GameThread t = Server.connected_games.get(resp.id);
					t.connected_playerInput.put(player, clientInput);
					t.connected_playerOutput.put(player, clientOutput);
					
					JoinGameResponse jgr = new JoinGameResponse(player, resp.id);
	    			for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    				jgr.send(value);
	    			}
				}
				else if (obj instanceof LogOut) {
					Server.connected_playerInput.remove(player);
					Server.connected_playerOutput.remove(player);
					clientInput.close();
					clientOutput.close();
					this.terminate();
				}
				else {
					//Handle request we don't understand
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
    }
    
    /**
     * Authenticates the user by checking if the credentials are valid according to the database.
     * Will create the Player Object if the credentials are valid
     * @return Boolean value. true if the credentials are valid, false otherwise
     * @author Shalin
     */
    private Boolean authenticate() throws IOException{
    	Boolean authenticate_status = false;
    	String username = null;
    	String password = null;
    	LoginMessage inputObject = null;
    	
    	try {
	        inputObject = (LoginMessage)clientInput.readObject();
	        username = inputObject.username;
	        password = inputObject.password;
	    	System.out.println("Recieved from client:" + username + password);
    		DatabaseConnection conn = Database.getConnection();
    		authenticate_status = conn.authenticateUser(username, password);
    		conn.close();
    	} catch (ClassNotFoundException e) {
        	e.printStackTrace();
        } catch (SQLException e){
        	e.printStackTrace();
        }
    	if (authenticate_status){
    		player = new Player(username);
    	}
    	return authenticate_status;
    }
    
    /**
     * Handles the cleanup when the thread closes.
     * This includes closing the socket and removing the player from the connect_players set
     * maintained by the main Server class.
     * @author Shalin
     */
    public void terminate() throws IOException{
		socket.close();
		Server.connected_players.remove(player);
    }
}
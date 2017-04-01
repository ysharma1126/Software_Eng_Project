package server;
import java.io.*;
import java.net.*;
import gamelogic.*;
import message.*;

import java.sql.*;

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
    
  //Requests for checking stats, joining a game, logging out, etc
    public void run() {
    	while (true) {
	    	Object obj;
	        //try (
	        //    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        //) {
	    	try {
	    		obj = (Object) clientInput.readObject();
	    		if (obj instanceof SignUpMessage) {
	    			SignUpMessage resp = (SignUpMessage) obj;
	    			DatabaseConnection conn = Database.getConnection();
	    			Boolean signup_status = conn.addUser(resp.username, resp.password);
	    			conn.close();
	    			if(signup_status) {
	    				SignUpResponse sr = new SignUpResponse(true, resp.username);
	        			sr.send(clientOutput);
	    			}
	    			else {
	    				SignUpResponse sr = new SignUpResponse(false, resp.username);
	        			sr.send(clientOutput);
	    			}
	    		}
	    		else if (obj instanceof LoginMessage) {
	    		    System.out.println("Entered Login Message");
	    			LoginMessage resp = (LoginMessage) obj;
	    			DatabaseConnection conn = Database.getConnection();
	    			Boolean authenticate_status = conn.authenticateUser(resp.username, resp.password);
	    			conn.close();
	    			System.out.println(authenticate_status);
	    			if(authenticate_status) {
	    				player = new Player(resp.username);
	    				
	    				LoginResponse lr = new LoginResponse(true, player.username);
	        			lr.send(clientOutput);
	        			Server.connected_playerInput.put(player, clientInput);
	        			Server.connected_playerOutput.put(player, clientOutput);
	        			
	        			GamesUpdateResponse gur = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
	        			gur.send(clientOutput);
	        			
	        			while (true) {
	        	        	try {
	        					obj = (Object) clientInput.readObject();
	        					//if (obj instanceof StatsRequest) {
	        					//	Stats stat = new Stats(this.getStats());
	        					//}
	        					if (obj instanceof CreateRoomMessage) {
	        						GameThread gt = new GameThread(player, socket, Server.gamesize);
	        		    			Thread t = new Thread(gt);
	        		    			t.start();
	        		    			
	        		    			Server.connected_games.put(Server.gamesize, gt);
	        		    			Server.connected_gamethreads.put(Server.gamesize, t);
	        		    			
	        		    			CreateRoomResponse cgr = new CreateRoomResponse(player, Server.gamesize);
	        		    			for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	        		    				cgr.send(value);
	        		    			}
	        		    			Server.gamesize++;
	        		    			t.join();
	        					}
	        					else if (obj instanceof JoinRoomMessage) {
	        						JoinRoomMessage resp2 = (JoinRoomMessage) obj;
	        						GameThread gt = Server.connected_games.get(resp2.gid);
	        						Thread t = Server.connected_gamethreads.get(resp2.gid);
	        						gt.connected_playerInput.put(player, clientInput);
	        						gt.connected_playerOutput.put(player, clientOutput);
	        						
	        						JoinRoomResponse jgr = new JoinRoomResponse(player, resp2.gid);
	        		    			for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	        		    				jgr.send(value);
	        		    			}
	        		    			
	        		    			t.join();
	        					}
	        					else if (obj instanceof LogOutMessage) {
	        						this.terminate();
	        			            return;
	        					}
	        					else if (obj instanceof RefreshMessage) {
	        						GamesUpdateResponse gur1 = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
	        	        			gur1.send(clientOutput);
	        					}
	        					else {
	        						//Handle request we don't understand
	        					}
	        				} catch (ClassNotFoundException e) {
	        					// TODO Auto-generated catch block
	        					e.printStackTrace();
	        				} catch (IOException e) {
	        					e.printStackTrace();
	        				}
	        	        	catch (InterruptedException e) {
	        					// TODO Auto-generated catch block
	        					e.printStackTrace();
	        				}
	        			}
	    			}
	    			else {
	    				LoginResponse lr = new LoginResponse(false, player.username);
	        			lr.send(clientOutput);
	    			}
	    				
	    		}		
	    	}
	    	catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	    	}	
	    	catch (IOException e) {
				// DISCONNECT
				e.printStackTrace();
	    	}
			catch (SQLException e) {
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

    /*private Boolean authenticate() throws IOException{
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
    }*/
    
    private void terminate() throws IOException {
    	socket.close();
    	Server.connected_playerInput.remove(player);
		Server.connected_playerOutput.remove(player);
    }
}
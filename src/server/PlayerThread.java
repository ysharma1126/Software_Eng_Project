package server;
import java.io.*;
import java.net.*;
import gamelogic.*;
import message.*;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

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
    public Thread thread = null;
    public ArrayBlockingQueue<Object> playerToGamePipe = null;
    public ArrayBlockingQueue<String> gameToPlayerPipe = null;
    
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
        playerToGamePipe = new ArrayBlockingQueue<Object>(1);
        gameToPlayerPipe = new ArrayBlockingQueue<String>(1);
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
	    		//First, check for SignUpMessage or LoginMessage
	    		obj = (Object) clientInput.readObject();
	    		if (obj instanceof SignUpMessage) {
	    			//Sign the user up, but don't automatically login
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
	    			//Removed 3 attempts, complicates debugging and imo not necessary
	    		    System.out.println("Entered Login Message");
	    			LoginMessage resp = (LoginMessage) obj;
	    			DatabaseConnection conn = Database.getConnection();
	    			Boolean authenticate_status = conn.authenticateUser(resp.username, resp.password);
	    			conn.close();
	    			System.out.println(authenticate_status);
	    			if(authenticate_status) {
	    				//If authenticated, create player object, add to hashmaps, and enter into the check for lobby messages
	    				player = new Player(resp.username);
	    				
	    				LoginResponse lr = new LoginResponse(true, player.username);
	        			lr.send(clientOutput);
	        			Server.connected_playerInput.put(player, clientInput);
	        			Server.connected_playerOutput.put(player, clientOutput);
	        			Server.connected_playerThread.put(player,Thread.currentThread());
	        			
	        			while(true) {
	        				//Once Client gets successful LoginResponse, sends GameUpdateMessage
	        				//GamesUpdateResponse gives client all necessary data, copied below
	        				//public Map <Long, Set<Player>> gameusernames;
	        				//public Map <Long, Player> gamehost;
	        				//public Set <Player> players;
	        				obj = (Object) clientInput.readObject();
	        				if (obj instanceof GamesUpdateMessage) {
	        					System.out.println("Initial GamesUpdateMessage");
	        					GamesUpdateResponse gur = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
	    	        			gur.send(clientOutput);
	    	        			System.out.println("Initial GamesUpdateResponse");
	    	        			//Now that client's updated, check for lobby actions
	    	        			while (true) {
	    	        	        	try {
	    	        					obj = (Object) clientInput.readObject();
	    	        					//if (obj instanceof StatsRequest) {
	    	        					//	Stats stat = new Stats(this.getStats());
	    	        					//}
	    	        					
	    	        					//On CreateRoomMessage, start game thread, add to hashmaps. send CreateRoomResponse to all connected players
	    	        					//POSSIBLE DEBUG: Unnecessarily sending response to players already in game might overflow buffer
	    	        					if (obj instanceof CreateRoomMessage) {
	    	        					    System.out.println("Got create room message");
	    	        						GameThread gt = new GameThread(player, clientInput, clientOutput, Server.gamesize, playerToGamePipe, gameToPlayerPipe);
	    	        		    			Thread t = new Thread(gt);
	    	        						t.start();
	    	        						
	    	        		    			System.out.println("Continue Player Thread");
	    	        		    			Server.connected_games.put(Server.gamesize, gt);
	    	        		    			Server.connected_gamethreads.put(Server.gamesize, t);
	    	        		    			
	    	        		    			CreateRoomResponse cgr = new CreateRoomResponse(player, Server.gamesize);
	    	        		    			System.out.println("sent create room response");
	    	        		    			for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    	        		    				cgr.send(value);
	    	        		    			}
	    	        		    			Server.gamesize++;
	    	        		    			//Once client in game, put this thread to sleep until game finishes
	    	        		    			//If client leaves game, interrupt sent, which is caught in interruptedexception
	    	        		    			gameMessageHandler();
	    	        		    			
	    	        		    			//Once playerthread wakes up, get refreshed
		    	        					GamesUpdateResponse gur1 = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
	    	        	        			gur1.send(clientOutput);
	    	        	        			System.out.println("Refresh Response");
	    	        					}
	    	        					//On JoinRoomMessage, find gamethread based on gid, add player to hashmaps, send JoinRoomResponse to all connected players
	    	        					//POSSIBLE DEBUG: Unnecessarily sending response to players already in game might overflow buffer
	    	        					else if (obj instanceof JoinRoomMessage) {
	    	        						JoinRoomMessage resp2 = (JoinRoomMessage) obj;
	    	        						GameThread gt = Server.connected_games.get(resp2.gid);
	    	        						Thread t = Server.connected_gamethreads.get(resp2.gid);
	    	        						gt.addNewPlayer(player, clientInput, clientOutput, playerToGamePipe, gameToPlayerPipe);
	    	        						
	    	        						JoinRoomResponse jgr = new JoinRoomResponse(player, resp2.gid);
	    	        		    			for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    	        		    				jgr.send(value);
	    	        		    			}
	    	        		    			//Once client in game, put this thread to sleep until game finishes
	    	        		    			//If client leaves game, interrupt sent, which is caught in interruptedexception
	    	        		    			gameMessageHandler();
	    	        		    			
	    	        		    			//Once playerthread wakes up, get refreshed
		    	        					GamesUpdateResponse gur1 = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
	    	        	        			gur1.send(clientOutput);
	    	        	        			System.out.println("Refresh Response");
	    	        					}
	    	        					//If client wants to logout, terminate connection and end player thread
	    	        					//DESIGN DECISION: LogOut option only in lobby, client disconnects need to be handled
	    	        					else if (obj instanceof LogOutMessage) {
	    	        						this.terminate();
	    	        			            return;
	    	        					}
	    	        					//Manual Refresh Button
	    	        					else if (obj instanceof RefreshMessage) {
	    	        						System.out.println("Refresh Message");
	    	        						GamesUpdateResponse gur1 = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
	    	        	        			gur1.send(clientOutput);
	    	        	        			System.out.println("Refresh Response");
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
	    	        	        	/* TODO Removed since no longer using Thread.join()
	    	        	        	catch (InterruptedException e) {
	    	        					//When gamethread sends interrupt, land here
	    	        					System.out.println("Interrupted Player Thread");
	    	        					// TODO Auto-generated catch block
	    	        					//Once playerthread interrupted, get refreshed
	    	        					GamesUpdateResponse gur1 = new GamesUpdateResponse(Server.connected_games, Server.connected_playerInput);
    	        	        			gur1.send(clientOutput);
    	        	        			System.out.println("Refresh Response");
	    	        					e.printStackTrace();
	    	        				}
	    	        				*/
	    	        			}
	        				}
	        			}
	    			}
	    			//If login fails, just tell client, do nothing
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
     * Handles communication between player thread and game thread
     * using the pipes
     * @author Shalin
     */
    void gameMessageHandler(){
    	String pipe_message; // holds recieved messages
    	Object obj = null; // holds messaged to be sent
    	try{
	    	while(true){
		    	while(gameToPlayerPipe.peek() == null){
		    		// do nothing
				}
		    	pipe_message = gameToPlayerPipe.poll();
		    	if (pipe_message == "leave"){
		    		break;
		    	}
		    	obj = (Object) clientInput.readObject();
		    	playerToGamePipe.put(obj);
	    	}
    	} catch (IOException e){
    		e.printStackTrace();
    	} catch (ClassNotFoundException e){
    		e.printStackTrace();
    	} catch (InterruptedException e) {
			e.printStackTrace();
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
    
    /**
     * Terminates user session by closing the socket and removing player from connected_player hashmaps
     * @author Shalin
     */
    
    private void terminate() throws IOException {
    	socket.close();
    	Server.connected_playerInput.remove(player);
		Server.connected_playerOutput.remove(player);
    }
}

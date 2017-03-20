package server;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import gamelogic.*;
import java.util.Collections;
import java.net.*;
import java.io.*;

/**
 * Server class handles keeping track of logged in users and the games that are running.
 * Spawns a Daemon thread to accept connection from client's and spawn PlayerThread objects.
 * @author Shalin
 */
public class Server {
	/**
	 * Plays the game. A game is initialized, the deck is created, and 12 cards are placed on the table.
	 * Then while the game is on, when the deck isn't empty or there is a set on the table, check whether there's a set on the table
	 * , if not deal 3 more cards. Wait for player to pick set, if valid, update set count, remove the 3 set cards, 
	 * and deal 3 more cards if there are less than 12 cards on the board. Once while loop is exited, the game has ended.
	 * Push game/player stats to DB, and send data to client for display
	 * @author		ysharma1126
	 * @param	p	Players who have entered this game
	 *
	 */
	/* TODO: commented out temporarily to avoid errors
		
	public void play(ArrayList <Player> p) {
		Game game = new Game();
		ArrayList <Card> deck = game.createDeck();
		ArrayList <Card> table = new ArrayList <Card>();
		game.dealCards(deck, table, 12);
		while(!deck.isEmpty() || game.checkSetexists(table)) {
			if (!game.checkSetexists(table)) {
				game.dealCards(deck, table, 3);
			}
			//prompt and wait for player p to pick cardset s
			if (game.validateSet(s)) {
				game.updateSetcount(p);
				game.removeCards(s, table);
				if (table.size() < 12) {
					game.dealCards(deck, table, 3);
				}
			}
		}
		// Push/Send game/player stats to DB/client
	}
	*/

	static Map<Player, Socket> connected_players = null;
	static int portNumber;

    /**
     * Main method that spawns a Daemon thread to add new clients.
     * What the method will do after is undecided as of now.
     * @param	args	[portNumber]
     * @author Shalin
     */
	public static void main(String[] args) throws IOException {
		if (args.length != 1){
			System.err.print("Usage: java Server <port Number>");
			System.exit(1);
		}

		connected_players = Collections.synchronizedMap(new HashMap<Player,Socket>());
		portNumber = Integer.parseInt(args[0]);
        Thread clientListener = new Thread(new clientListenerThread(), "clientListener");
        clientListener.setDaemon(true);
        clientListener.start();
        while(true){
        	// DO STUFF
        }
    }
	
    /**
     * The thread that accepts connections on the serverSocket and spawns a PlayerThread foreach connection.
     * @author Shalin
     */
	static class clientListenerThread implements Runnable {
		public void run(){
	        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
	        	while(true){
	    			Socket s = serverSocket.accept();
	    			Thread t = new Thread(new PlayerThread(s));
	    			t.start();
				}
		    } catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(-1);
	        }
		}
	}
	
}
package server;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	//GID, every created game/room increments this, made a long so it will practically never overfill
	static long gamesize = 0;
	//More Practical to Hashmap to the streams after they've been instantiated than the socket
    static Map<Player, ObjectInputStream> connected_playerInput = null;
    static Map<Player, ObjectOutputStream> connected_playerOutput = null;
    //Keep track of actual thread for sleeping/waking up from other threads
    static Map<Player, Thread> connected_playerThread = null;
    //Keep track of games
	static Map<Long, GameThread> connected_games = null;
	//Keep track of actual thread for sleeping/waking up from other threads
	static Map<Long, Thread> connected_gamethreads = null;
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
		connected_games = Collections.synchronizedMap(new HashMap<Long, GameThread>());
		connected_gamethreads = Collections.synchronizedMap(new HashMap<Long, Thread>());
		connected_playerInput = Collections.synchronizedMap(new HashMap<Player,ObjectInputStream>());
		connected_playerOutput = Collections.synchronizedMap(new HashMap<Player,ObjectOutputStream>());
		connected_playerThread = Collections.synchronizedMap(new HashMap<Player,Thread>());
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
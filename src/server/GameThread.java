package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import gamelogic.*;
import message.LeaveGameResponse;
import message.StartGameResponse;
import message.TableResponse;

/**
 * A thread for a single game
 * Handles communication with the client for things pertaining to the game
 * @author Yash
 *
 */

public class GameThread implements Runnable {
	
    static Map<Player, ObjectInputStream> connected_playerInput = null;
    static Map<Player, ObjectOutputStream> connected_playerOutput = null;
    private Player hostp = null;
    private Socket hosts = null;
    public ObjectInputStream hostInput = null;
    public ObjectOutputStream hostOutput = null;
    int gid;
    
    public GameThread(Player p, Socket s, int id) throws IOException {
    	hostp = p;
    	hosts = s;
    	gid = id;
    	
        hostInput = new ObjectInputStream(hosts.getInputStream());
        hostOutput = new ObjectOutputStream(hosts.getOutputStream());
        connected_playerInput = Collections.synchronizedMap(new HashMap<Player,ObjectInputStream>());
		connected_playerOutput = Collections.synchronizedMap(new HashMap<Player,ObjectOutputStream>());
		Server.connected_playerInput.put(p, hostInput);
		Server.connected_playerOutput.put(p, hostOutput);
    }

	public void run() {
		while(true) {
			Object obj;
			try {
				obj = (Object) hostInput.readObject();
				if (obj instanceof StartGame) {
					StartGameResponse sgr = new StartGameResponse(gid);
					for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    				sgr.send(value);
	    			}
					
					Game game = new Game();
					ArrayList <Card> deck = game.createDeck();
					ArrayList <Card> table = new ArrayList <Card>();
					game.dealCards(deck, table, 12);
					
					TableResponse tr = new TableResponse(table);
					for(ObjectOutputStream value : GameThread.connected_playerOutput.values()) {
	    				tr.send(value);
	    			}
					while (true) {
						while(!deck.isEmpty() || game.checkSetexists(table)) {
							if (!game.checkSetexists(table)) {
								game.dealCards(deck, table, 3);
								
								TableResponse tr = new TableResponse(table);
								for(ObjectOutputStream value : GameThread.connected_playerOutput.values()) {
				    				tr.send(value);
				    			}
							}
							for (Map.Entry<Player, ObjectInputStream> entry: GameThread.connected_playerInput.entrySet()) {
								obj = (Object) entry.getValue().readObject();
								if (obj instanceof SetSelectMessage) {
									
								}
							}
						}
					}
				}
				for (Map.Entry<Player, ObjectInputStream> entry: GameThread.connected_playerInput.entrySet()) {
					obj = (Object) entry.getValue().readObject();
					if (obj instanceof LeaveGame) {
						LeaveGameResponse lgr = new LeaveGameResponse(entry.getKey());
						for(ObjectOutputStream value : GameThread.connected_playerOutput.values()) {
		    				lgr.send(value);
		    			}
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
	}

}

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
import message.*;

/**
 * A thread for a single game
 * Handles communication with the client for things pertaining to the game
 * @author Yash
 */

public class GameThread implements Runnable {
	
    public Map<Player, ObjectInputStream> connected_playerInput = null;
    public Map<Player, ObjectOutputStream> connected_playerOutput = null;
    public Player hostp = null;
    private ObjectInputStream hostInput = null;
    private ObjectOutputStream hostOutput = null;
    int gid;
    
    public GameThread(Player p, ObjectInputStream i, ObjectOutputStream o, int id) throws IOException {
    	hostp = p;
    	gid = id;
    	System.out.println("1");
        hostInput = i;
        System.out.println("2");
        hostOutput = o;
        System.out.println("3");
        connected_playerInput = Collections.synchronizedMap(new HashMap<Player,ObjectInputStream>());
        System.out.println("4");
		connected_playerOutput = Collections.synchronizedMap(new HashMap<Player,ObjectOutputStream>());
        System.out.println("5");
		connected_playerInput.put(p, hostInput);
        System.out.println("6");
		connected_playerOutput.put(p, hostOutput);
        System.out.println("end");
    }

	public void run() {
		System.out.println("wtf");
		while(true) {
			System.out.println("In while loop");
			Object obj;
			try {
				
				obj = (Object) hostInput.readObject();
				if (obj instanceof StartGameMessage) {
					System.out.println("In StartGame");
					StartGameResponse sgr = new StartGameResponse(gid);
					for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    				sgr.send(value);
	    			}
					System.out.println("Sent Message");
					Game game = new Game();
					ArrayList <Card> deck = game.createDeck();
					ArrayList <Card> table = new ArrayList <Card>();
					game.initTable(deck, table, 12);
					System.out.println("Setup Table");
					//Send cards when Sahil asks
					int check = 0;
					System.out.println(this.connected_playerInput.size());
					while(!(check == this.connected_playerInput.size())) {
						for (Map.Entry<Player, ObjectInputStream> entry: this.connected_playerInput.entrySet()) {
							obj = (Object) entry.getValue().readObject();
							if (obj instanceof InitialCardsMessage) {
								System.out.println("Sent Initial Cards Response");
								check++;
								InitialCardsResponse icr = new InitialCardsResponse(table);
								icr.send(this.connected_playerOutput.get(entry.getKey()));
							}
						}
					}
					while (true) {
						System.out.println("Starting Game");
						while(!deck.isEmpty() || game.checkSetexists(table)) {
							if (!game.checkSetexists(table)) {
								System.out.println("Need more cards");
								game.dealCards(deck, table, 3);
								ArrayList <Card> table1 = new ArrayList <Card>();
								for (Card card: table) {
									table1.add(card);
								}
								
								TableResponse tr1 = new TableResponse(table1);
								for(Map.Entry<Player, ObjectOutputStream> entry: this.connected_playerOutput.entrySet()) {
									if (entry.getKey().setcount != -1) {
										tr1.send(entry.getValue());
									}
				    			}
							}
							System.out.println("Player Set Size");
							System.out.println(this.connected_playerInput.size());
							for (Map.Entry<Player, ObjectInputStream> entry: this.connected_playerInput.entrySet()) {
								obj = (Object) entry.getValue().readObject();
								if (obj instanceof SetSelectMessage) {
									System.out.println("Received a set");
									SetSelectMessage resp = (SetSelectMessage) obj;
									if(game.validateSet(resp.cards)) {
										System.out.println("Set's valid!");
										game.updateSetcount(entry.getKey());
										
										SetSelectResponse ssr = new SetSelectResponse(entry.getKey(), true);
										for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											if (entry1.getKey().setcount != -1) {
												ssr.send(entry1.getValue());
											}
						    			}
										for(Card card: table) {
											System.out.println(card.toImageFile());
										}
										game.removeCards(resp.cards, table);
										System.out.println("Table Size");
										System.out.println(game.getsize(table));
										for(Card card: table) {
											System.out.println(card.toImageFile());
										}
										if (game.getsize(table) < 12 && !deck.isEmpty()) {
											game.replaceCards(resp.cards, deck, table);
										}
										ArrayList <Card> table1 = new ArrayList<Card>();
										for (Card card: table) {
											table1.add(card);
										}
										TableResponse tr2 = new TableResponse(table1);
										for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											if (entry1.getKey().setcount != -1) {
												tr2.send(entry1.getValue());
											}
						    			}
						    			
									}
									else {
										System.out.println("Set invalid");
										SetSelectResponse ssr = new SetSelectResponse(entry.getKey(), false);
										for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											if (entry1.getKey().setcount != -1) {
												ssr.send(entry1.getValue());
											}
						    			}
									}
								}
								
								if (obj instanceof LeaveGameMessage) {
									entry.getKey().setcount = -1;
									
									LeaveGameResponse lgr = new LeaveGameResponse(entry.getKey());
									for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
										if (entry1.getKey().setcount != -1) {
											lgr.send(entry1.getValue());
										}
					    			}
								}
							}
						}
						
						EndGameResponse eg = new EndGameResponse();
						for(Map.Entry<Player, ObjectOutputStream> entry: this.connected_playerOutput.entrySet()) {
							if (entry.getKey().setcount != -1) {
								eg.send(entry.getValue());
							}
		    			}
						this.terminate();
						//Push Stats to DB
						return;
					}
				}
				
				for (Map.Entry<Player, ObjectInputStream> entry: this.connected_playerInput.entrySet()) {
					System.out.println("In for loop");
					obj = (Object) entry.getValue().readObject();
					if (obj instanceof LeaveRoomMessage) {
						System.out.println("In LeaveRoomMessage");
						this.connected_playerInput.remove(entry.getKey());
						this.connected_playerOutput.remove(entry.getKey());
						
						LeaveRoomResponse lrr = new LeaveRoomResponse(entry.getKey());
						for(ObjectOutputStream value : this.connected_playerOutput.values()) {
		    				lrr.send(value);
		    			}
					}
				}
				System.out.println("Out of for loop");
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	// TODO Auto-generated method stub
	}
	
	private void terminate() throws IOException {
		Server.connected_games.remove(gid);
    }

}



package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

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
    public Map<Player, ArrayBlockingQueue<String> > connected_playerPipe = null;
    public Player hostp = null;
    private ObjectInputStream hostInput = null;
    private ObjectOutputStream hostOutput = null;
    long gid;
    
    /**
     * Initializes the GameThread. Keeps track of the host and gid
     * @param	p	host
     * @param	i	hostinputstream
     * @param	o	hostoutputstream
     * @param	id	gid
     * @author Yash
     */
    public GameThread(Player p, ObjectInputStream i, ObjectOutputStream o, long id, ArrayBlockingQueue<String> pipe) throws IOException {
    	hostp = p;
    	gid = id;
        hostInput = i;
        hostOutput = o;
        connected_playerInput = Collections.synchronizedMap(new HashMap<Player,ObjectInputStream>());
		connected_playerOutput = Collections.synchronizedMap(new HashMap<Player,ObjectOutputStream>());
		connected_playerPipe = Collections.synchronizedMap(new HashMap<Player, ArrayBlockingQueue<String>>());
		connected_playerInput.put(p, hostInput);
		connected_playerOutput.put(p, hostOutput);
		connected_playerPipe.put(p, pipe);
    }

	public void run() {
		//Handles Room Actions
		while(true) {
			//System.out.println("In while loop");
			Object obj;
			try {
				//Check host, as only host can start game
				obj = (Object) hostInput.readObject();
				if (obj instanceof StartGameMessage) {
					System.out.println("Received Start Game Message");
					StartGameResponse sgr = new StartGameResponse(gid);
					//Sent StartGameResponse to all players
					//POSSIBLE DEBUG: Unnecessarily sending response to players already in game might overflow buffer
					for(ObjectOutputStream value : Server.connected_playerOutput.values()) {
	    				sgr.send(value);
	    			}
					System.out.println("Sent Start Game Response");
					//Game Logic
					Game game = new Game();
					ArrayList <Card> deck = game.createDeck();
					ArrayList <Card> table = new ArrayList <Card>();
					game.initTable(deck, table, 12);
					System.out.println("Setup Table");
					//Game only starts when server receives initialcardsmessages from each of the players in the game
					//On InitialCardsMessage, send InitialCardsResponse, simply initial state of the table
					int check = 0;
					System.out.println(this.connected_playerInput.size());
					while(!(check == this.connected_playerInput.size())) {
						for (Map.Entry<Player, ObjectInputStream> entry: this.connected_playerInput.entrySet()) {
							obj = (Object) entry.getValue().readObject();
							if (obj instanceof InitialCardsMessage) {
								System.out.println("Received Initial Cards Message");
								check++;
								InitialCardsResponse icr = new InitialCardsResponse(table);
								icr.send(this.connected_playerOutput.get(entry.getKey()));
								System.out.println("Sent Initial Cards Response");
							}
						}
					}
					//Handles Game Actions
					while (true) {
						System.out.println("Starting Game");
						//Game only ends when deck is empty and no set exists on the table
						while(!deck.isEmpty() || (game.checkSetexists(table).size() > 0)) {
							//No set on table, if there's no set on table, must be at least 3 cards in deck
							if (game.checkSetexists(table).size() == 0) {
								System.out.println("No Set on Table");
								game.dealCards(deck, table, 3);
								//This is just because sending table to client didn't work, dumb solution after hours of frustration, don't question it
								ArrayList <Card> table1 = new ArrayList <Card>();
								for (Card card: table) {
									table1.add(card);
								}
								//Send Updated table to all players currently in game
								TableResponse tr1 = new TableResponse(table1);
								for(Map.Entry<Player, ObjectOutputStream> entry: this.connected_playerOutput.entrySet()) {
									tr1.send(entry.getValue());
				    			}
								//Check again if the game needs to continue, and if so, if 3 more cards need to be dealt to the table
								//If the game is live, there should always be a set on the board
								continue;
							}
							ArrayList <Card> temp = new ArrayList <Card>();	
							//checkSetexists returns set, returns 0 if no set, hence can be used as a check as well
							//optimizes testing out game, finding a set is hard
							temp = game.checkSetexists(table);
							for (Card card: temp) {
								System.out.println(card.toImageFile());
							}
							//Check for messages from each player
							for (Map.Entry<Player, ObjectInputStream> entry: this.connected_playerInput.entrySet()) {
								obj = (Object) entry.getValue().readObject();
								Player player = entry.getKey();
								ArrayBlockingQueue<String> pipe = connected_playerPipe.get(player);
								pipe.add("HELLO WORLD!");
								if (obj instanceof SetSelectMessage) {
									System.out.println("Received a set");
									SetSelectMessage resp = (SetSelectMessage) obj;
									//If set's valid, update set count, send to all players in SetSelectResponse
									if(game.validateSet(resp.cards)) {
										System.out.println("Set's valid!");
										game.updateSetcount(entry.getKey());
										
										SetSelectResponse ssr = new SetSelectResponse(entry.getKey(), true);
										for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											ssr.send(entry1.getValue());
						    			}
										for(Card card: table) {
											System.out.println(card.toImageFile());
										}
										//If set's valid, remove cards from table
										//In order to make board configuration intuitive, we had to figure out how to make it so that if the set gets
										//replaced it's done realistically, like the 3 cards are literally replaced on the board, the cards aren't shifted
										//around unrealistically. 
										
										//To put things short, we put a hole attribute in card, and changed removecard to set hole attribute to true
										game.removeCards(resp.cards, table);
										System.out.println("Table Size");
										//Overloaded size function needed as holes need to be manually not accounted for when calculating number of cards
										//on table
										System.out.println(game.getsize(table));
										for(Card card: table) {
											System.out.println(card.toImageFile());
										}
										//If less than 12 cards on table and there are cards in the deck, REPLACE the holes. Function takes next 3 cards
										//in deck and places it in place of the holes
										if (game.getsize(table) < 12 && !deck.isEmpty()) {
											game.replaceCards(resp.cards, deck, table);
										}
										ArrayList <Card> table1 = new ArrayList<Card>();
										for (Card card: table) {
											table1.add(card);
										}
										//Send updated table to all players in game
										TableResponse tr2 = new TableResponse(table1);
										for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											tr2.send(entry1.getValue());
						    			}
						    			
									}
									else {
										//If set invalid, only tell client
										System.out.println("Set invalid");
										SetSelectResponse ssr = new SetSelectResponse(entry.getKey(), false);
										/*for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											ssr.send(entry1.getValue());
						    			}*/
										ssr.send(this.connected_playerOutput.get(entry.getKey()));
									}
								}
								//Users also have option to leave game midway, surrender
								if (obj instanceof LeaveGameMessage) {
									//Set setcount to -1, punishment for raging
									entry.getKey().setcount = -1;
									
									//Tell all players client has left game
									LeaveGameResponse lgr = new LeaveGameResponse(entry.getKey());
									for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
										lgr.send(entry1.getValue());
					    			}
									
									//No need for this as host has no special abilities once the game has started
									/*
									if (entry.getKey() == this.hostp) {
										for (Map.Entry<Player, ObjectInputStream> entryy: this.connected_playerInput.entrySet()) {
											if (entryy.getKey() != entry.getKey()) {
												this.hostp = entryy.getKey();
												this.hostInput = entryy.getValue();
												this.hostOutput = this.connected_playerOutput.get(entryy.getKey());
												break;
											}
										}
										
										this.connected_playerInput.remove(entry.getKey());
										this.connected_playerOutput.remove(entry.getKey());
										
										ChangedHostResponse chr = new ChangedHostResponse(this.hostp);
										for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
											chr.send(entry1.getValue());
						    			}
									}
									else {
										this.connected_playerInput.remove(entry.getKey());
										this.connected_playerOutput.remove(entry.getKey());
									}*/
									
									//If only 1 player in game, if player leaves, close game
									if (this.connected_playerInput.size() == 1) {
										this.terminate();
										return;
									}

									this.connected_playerInput.remove(entry.getKey());
									this.connected_playerOutput.remove(entry.getKey());
									//Need to wakeup player thread, even though game thread isnt done, so interrupt
									Server.connected_playerThread.get(entry.getKey()).interrupt();
								}
							}
						}
						//Game's over
						ArrayList <Card> table1 = new ArrayList<Card>();
						for (Card card: table) {
							table1.add(card);
						}
						
						//Send final table response
						TableResponse tr2 = new TableResponse(table1);
						for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
							if (entry1.getKey().setcount != -1) {
								tr2.send(entry1.getValue());
							}
		    			}
						
						//Send EndGameResponse to all players in game
						EndGameResponse eg = new EndGameResponse();
						for(Map.Entry<Player, ObjectOutputStream> entry: this.connected_playerOutput.entrySet()) {
							if (entry.getKey().setcount != -1) {
								eg.send(entry.getValue());
							}
		    			}
						//Terminate and end game thread
						this.terminate();
						//Push Stats to DB
						return;
					}
				}
				//In Room, check with all the players if they want to leave
				for (Map.Entry<Player, ObjectInputStream> entry: this.connected_playerInput.entrySet()) {
					//System.out.println("In for loop");
					obj = (Object) entry.getValue().readObject();
					if (obj instanceof LeaveRoomMessage) {
						System.out.println("Received LeaveRoomMessage");
						//Tell all players client leaving
						LeaveRoomResponse lrr = new LeaveRoomResponse(entry.getKey());
						for(ObjectOutputStream value : this.connected_playerOutput.values()) {
		    				lrr.send(value);
		    			}
						
						System.out.println("Sent LeaveRoomResponse");
						
						//If only 1 player in room, if player leaves, close room
						if (this.connected_playerInput.size() == 1) {
							this.terminate();
							return;
						}
						
						//If host leaves room, find another player and set them to be the host
						if (entry.getKey() == this.hostp) {
							for (Map.Entry<Player, ObjectInputStream> entryy: this.connected_playerInput.entrySet()) {
								if (entryy.getKey() != entry.getKey()) {
									this.hostp = entryy.getKey();
									this.hostInput = entryy.getValue();
									this.hostOutput = this.connected_playerOutput.get(entryy.getKey());
									break;
								}
							}
							
							this.connected_playerInput.remove(entry.getKey());
							this.connected_playerOutput.remove(entry.getKey());										
							
							//Send ChangedHostResponse, telling all clients who the new host is
							ChangedHostResponse chr = new ChangedHostResponse(this.hostp);
							for(Map.Entry<Player, ObjectOutputStream> entry1: this.connected_playerOutput.entrySet()) {
								chr.send(entry1.getValue());
			    			}
						}
						else {
							this.connected_playerInput.remove(entry.getKey());
							this.connected_playerOutput.remove(entry.getKey());
						}

						//Need to wakeup player thread, even though game thread isnt done, so interrupt
						Server.connected_playerThread.get(entry.getKey()).interrupt();
					}
				}
				//System.out.println("Out of for loop");
			}
			  catch (ClassNotFoundException | IOException e) {
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



package message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import server.*;
import gamelogic.*;

public class GamesUpdateResponse implements Sendable, Serializable {
	
	public Map <Long, Set<Player>> gameusernames;
	public Map <Long, Player> gamehost;
	public Set <Player> players;
	
	
  public GamesUpdateResponse(Map <Long, GameThread> games, Map <Player, ObjectInputStream> players) {
    Map <Long, Set<Player>> temp1 = new HashMap<Long, Set<Player>>();
    Map <Long, Player> temp2 = new HashMap<Long, Player>();
    Set <Player> temp3 = new HashSet<Player>();
    for (Map.Entry<Long, GameThread> entry: games.entrySet()) {
    	Set <Player> tempset = new HashSet<Player>(entry.getValue().connected_playerInput.keySet());
    	temp1.put(entry.getKey(), tempset);
    	temp2.put(entry.getKey(), entry.getValue().hostp);
    }
    for (Map.Entry<Player, ObjectInputStream> entry: players.entrySet()) {
    	temp3.add(entry.getKey());
    }
    this.players = temp3;
    this.gameusernames = temp1;
    this.gamehost = temp2;
    
  }
  
  public void send(ObjectOutputStream outputstream)
  {
    try {
      outputstream.writeObject(this);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}

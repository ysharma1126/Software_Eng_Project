package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import server.*;
import gamelogic.*;

public class GamesUpdateResponse implements Sendable {
	
	public Map <Integer, Set<Player>> gameusernames;
	public Map <Integer, Player> gamehost;
	
	
  public GamesUpdateResponse(Map <Integer, GameThread> games) {
    Map <Integer, Set<Player>> temp1 = new HashMap<Integer, Set<Player>>();
    Map <Integer, Player> temp2 = new HashMap<Integer, Player>();
    for (Map.Entry<Integer, GameThread> entry: games.entrySet()) {
    	temp1.put(entry.getKey(), entry.getValue().connected_playerInput.keySet());
    	temp2.put(entry.getKey(), entry.getValue().hostp);
    }
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

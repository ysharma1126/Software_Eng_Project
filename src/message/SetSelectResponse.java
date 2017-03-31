package message;

import java.io.IOException;
import java.io.ObjectOutputStream;

import gamelogic.Player;

public class SetSelectResponse implements Sendable {
	
	public String username;
	public int setcount;
	
  public SetSelectResponse(Player p) {
    this.setcount = p.setcount;
    this.username = p.username;
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

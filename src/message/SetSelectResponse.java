package message;

import java.io.IOException;
import java.io.ObjectOutputStream;

import gamelogic.Player;

public class SetSelectResponse implements Sendable {
	
	public String username;
	public int setcount;
	public boolean is_valid;
	
	
  public SetSelectResponse(Player p, boolean is_valid) {
    this.setcount = p.setcount;
    this.username = p.username;
    this.is_valid = is_valid;
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

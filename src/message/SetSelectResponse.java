package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import gamelogic.*;

public class SetSelectResponse implements Sendable, Serializable {
	
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
      outputstream.writeUnshared(this);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}

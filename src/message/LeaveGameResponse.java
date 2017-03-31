package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import gamelogic.*;

public class LeaveGameResponse implements Sendable, Serializable{
	
	public String uname;
	
	public LeaveGameResponse(Player p) {
		uname = p.username;
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

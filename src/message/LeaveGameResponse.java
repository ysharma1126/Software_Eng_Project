package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import gamelogic.*;

public class LeaveGameResponse implements Sendable, Serializable{
	
	public String uname;
	public int setcount;
	
	public LeaveGameResponse(Player p) {
		uname = p.username;
		setcount = p.setcount;
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

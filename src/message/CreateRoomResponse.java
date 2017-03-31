package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import gamelogic.*;

public class CreateRoomResponse implements Sendable, Serializable{
	
	public String uname;
	public int gid;
	
	public CreateRoomResponse(Player p, int id) {
		uname = p.username;
		gid = id;
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

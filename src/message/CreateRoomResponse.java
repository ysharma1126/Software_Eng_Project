package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import gamelogic.*;

public class CreateRoomResponse implements Sendable, Serializable{
	
	public String uname;
	public long gid;
	
	public CreateRoomResponse(Player p, long id) {
		uname = p.username;
		gid = id;
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

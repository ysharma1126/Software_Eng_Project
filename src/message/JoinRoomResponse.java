package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import gamelogic.*;

public class JoinRoomResponse implements Sendable, Serializable{
	
	public String uname;
	public Long gid;
	public boolean is_valid;
	
	public JoinRoomResponse(Player p, Long id, boolean is_v) {
		uname = p.username;
		gid = id;
		is_valid = is_v;
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

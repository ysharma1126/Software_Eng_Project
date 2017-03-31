package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import gamelogic.*;

public class StartGameResponse implements Sendable, Serializable{
	
	public int gid;
	
	public StartGameResponse(int id) {
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

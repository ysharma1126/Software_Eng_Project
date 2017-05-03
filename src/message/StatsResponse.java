package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/* Stats
* Should be sent only to the client that sent a 
* StatsRequest to the server.
* Stats should send the stats from the DB to the client
*/

public class StatsResponse implements Sendable, Serializable {
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

package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import gamelogic.*;

public class InitialCardsResponse implements Sendable, Serializable{
	
	public CopyOnWriteArrayList<Card> table;
	
	public InitialCardsResponse(CopyOnWriteArrayList <Card> t) {
		this.table = t;
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

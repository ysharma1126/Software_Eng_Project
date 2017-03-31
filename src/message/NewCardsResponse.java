package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import gamelogic.*;

public class NewCardsResponse implements Sendable, Serializable{
	
	public ArrayList<Card> table;
	
	public NewCardsResponse(ArrayList <Card> t) {
		table = t;
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

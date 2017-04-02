package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import gamelogic.*;

public class TableResponse implements Sendable, Serializable{
	
	public ArrayList<Card> table;
	public double randomnum;
	
	public TableResponse(ArrayList <Card> t) {
		this.table = t;
		this.randomnum = Math.random();
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

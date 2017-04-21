package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import gamelogic.*;

public class TableResponse implements Sendable, Serializable{
	
	public ArrayList<Card> table1;
	public double randomnum;
	
	public TableResponse(ArrayList <Card> t) {
		this.table1 = t;
	}
	
	public void send(ObjectOutputStream outputstream)
	  {
		this.randomnum = Math.random();
		for(Card card: this.table1) {
			System.out.println(card.getDescription());
		}
	    try {
	      outputstream.writeObject(this);
	    } catch (IOException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	  }
}

package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import gamelogic.*;

public class SetSelectResponse implements Sendable {
	
	public String username;
	public int setcount;
	public boolean is_valid;
	public ArrayList <Card> cards;
	

  public SetSelectResponse(Player p, boolean is_valid, ArrayList <Card> cards) {
    this.setcount = p.setcount;
    this.username = p.username;
    this.is_valid = is_valid;
    this.cards = cards;
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

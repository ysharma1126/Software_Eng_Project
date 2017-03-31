package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import gamelogic.*;

public class SetSelectResponse implements Sendable {
	
	public String username;
	public int setcount;
	public boolean is_valid;
	
<<<<<<< HEAD
  public SetSelectResponse(Player p, boolean is_valid) {
=======

  public SetSelectResponse(Player p, boolean is_valid, ArrayList <Card> cards) {
>>>>>>> branch 'master' of https://github.com/sahpat229/Software_Eng_Project.git
    this.setcount = p.setcount;
    this.username = p.username;
    this.is_valid = is_valid;
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

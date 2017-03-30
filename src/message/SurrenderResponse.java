package message;

import java.io.IOException;
import java.io.ObjectOutputStream;


/*
 * Surrender Response
 * Should be broadcasted to all available
 * clients for a particular game room
 * Once a user surrenders.
 * this.username contains the username of the user that surrendered.
 */

public class SurrenderResponse implements Sendable {
  
  public String username;
  
  public SurrenderMessage(String username, int cards[])
  {
    this.username = username;
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

package message;

import java.io.IOException;
import java.io.ObjectOutputStream;

/*
 * SurrenderMessage
 * Contains the username of the client that surrendered
 */

public class LeaveGame implements Sendable {
  
  public String username;
  
  public LeaveGame(String username)
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

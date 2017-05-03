package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * SurrenderMessage
 * Contains the username of the client that surrendered
 */

public class LeaveGameMessage implements Sendable, Serializable {
  
  public String username;
  
  public LeaveGameMessage(String username)
  {
    this.username = username;
  }
  
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

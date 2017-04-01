package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * LoginMessage
 * A Client sends a login message to the server
 * that contains the username and password that the user
 * Is trying to login with
 */

public class GamesUpdateMessage implements Sendable, Serializable {
  
  public GamesUpdateMessage()
  {
  }
  
  @Override
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
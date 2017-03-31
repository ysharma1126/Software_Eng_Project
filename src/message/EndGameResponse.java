package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * LoginResponse
 * Should be sent only to the client that sent a 
 * LoginMessage to the server.
 * LoginResponse should say whether the login was valid or not
 */

public class EndGameResponse implements Sendable, Serializable {
  
  public EndGameResponse()
  {}
  
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
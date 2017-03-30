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

public class LoginResponse implements Sendable, Serializable {

  public boolean is_valid;
  
  public LoginResponse(boolean is_valid)
  {
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
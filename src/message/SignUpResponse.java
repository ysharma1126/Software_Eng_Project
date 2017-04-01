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

public class SignUpResponse implements Sendable, Serializable {

  public boolean is_valid;
  public String username;
  
  public SignUpResponse(boolean is_valid, String username)
  {
    this.is_valid = is_valid;
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
package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * SignUp Message
 * A Client sends a login message to the server
 * that contains the username and password that the user
 * Is trying to login with
 */

public class SignUpMessage implements Sendable, Serializable {
  
  public String username;
  public String password;
  
  public SignUpMessage(String username, String password)
  {
    this.username = username;
    this.password = password;
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

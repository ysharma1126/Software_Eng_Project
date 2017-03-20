package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LoginMessage implements Sendable, Serializable {
  
  public String username;
  public String password;
  
  public LoginMessage(String username, String password)
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
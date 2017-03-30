package ui;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginMessage implements Sendable {
  
  private String username;
  private String password;
  
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
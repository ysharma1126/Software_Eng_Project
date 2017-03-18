package Frontend;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Login_Message implements Sendable {
  
  private String username;
  private String password;
  
  public Login_Message(String username, String password)
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
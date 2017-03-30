package ui;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LoginResponse implements Sendable {

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

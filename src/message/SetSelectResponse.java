package message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SetSelectResponse implements Sendable {

  public boolean is_valid;
  public String username;
  
  public SetSelectResponse(String username, boolean is_valid)
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

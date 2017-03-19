package Frontend;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Initial_Cards_Message implements Sendable {

  public Initial_Cards_Message()
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

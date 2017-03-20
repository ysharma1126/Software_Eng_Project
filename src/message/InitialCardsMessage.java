package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InitialCardsMessage implements Sendable, Serializable {

  public InitialCardsMessage()
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

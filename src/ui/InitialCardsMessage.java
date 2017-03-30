package ui;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class InitialCardsMessage implements Sendable {

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

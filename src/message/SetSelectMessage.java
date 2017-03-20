package message;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SetSelectMessage implements Sendable {

  public int cards[];
  
  public SetSelectMessage(int cards[])
  {
    this.cards = cards;
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

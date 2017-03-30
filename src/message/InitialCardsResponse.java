package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class InitialCardsResponse implements Sendable, Serializable {
  
  public int cards[] = new int[12];
    
  public InitialCardsResponse(int cards[])
  {
    this.cards = cards;
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

package Frontend;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class Initial_Cards_Response implements Sendable {
  
  public int cards[] = new int[12];
    
  public Initial_Cards_Response(int cards[])
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

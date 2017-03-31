package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * StartGame
 * A ping from a client to the server to send it the twelve initial
 * Cards for a game.  The server should make sure that all clients got
 * The initial cards before starting the game.
 */

public class StartGameMessage implements Sendable, Serializable {

  public StartGameMessage()
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

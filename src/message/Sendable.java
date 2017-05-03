package message;

import java.io.ObjectOutputStream;

public interface Sendable {

  public void send(ObjectOutputStream outputstream);
  
}

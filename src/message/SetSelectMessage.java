package message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import gamelogic.Card;

/*
 * SetSelectMessage
 * Contains the cards and username
 * That a certain client picked.
 */

public class SetSelectMessage implements Sendable {
	
  //Change to public ArrayList <Card> cards;
  public ArrayList <Card> cards;
  public String username;
  
//Change to public ArrayList <Card> cards;
  public SetSelectMessage(String username, ArrayList <Card> cards)
  {
    this.username = username;
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

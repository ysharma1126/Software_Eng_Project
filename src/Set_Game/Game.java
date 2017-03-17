package Set_Game;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Game class, functions needed to control the Set game
 * @author ysharma1126
 * 
 *
 */
public class Game {
	/**
	 * Creates and shuffles the deck. Meant to be used at the start of the game. 
	 * @author		ysharma1126
	 * @return 		Arraylist of cards representing the deck
	 *
	 */
 public ArrayList <Card> createDeck() {
		ArrayList <Card> cards = new ArrayList <Card> (81);
		for (int shape = 0; shape < 3; shape++) {
			for (int number = 0; number < 3; number++) {
				for (int color = 0; color < 3; color++) {
					for (int shading = 0; shading < 3; shading++) {
						cards.add(new Card(shape, number, color, shading));
					}
				}
				
			}
		}
		Collections.shuffle(cards);
		return cards;
	}
	/**
	 * Checks to see if a card trio is a set
	 * @author		ysharma1126
	 * @param	a	card in question 
	 * @param	b	card in question
	 * @param	c	card in question
	 * @return		boolean representing whether the card trio is a set
	 *
	 */
	boolean validateSet(ArrayList <Card> set) {
		if (!((set.get(0).shape == set.get(1).shape) && (set.get(1).shape == set.get(2).shape) ||
                (set.get(0).shape != set.get(1).shape) && (set.get(0).shape != set.get(2).shape) && (set.get(1).shape != set.get(2).shape))) {
            return false;
        }
        if (!((set.get(0).number == set.get(1).number) && (set.get(1).number == set.get(2).number) ||
                (set.get(0).number != set.get(1).number) && (set.get(0).number != set.get(2).number) && (set.get(1).number != set.get(2).number))) {
            return false;
        }
        if (!((set.get(0).color == set.get(1).color) && (set.get(1).color == set.get(2).color) ||
                (set.get(0).color != set.get(1).color) && (set.get(0).color != set.get(2).color) && (set.get(1).color != set.get(2).color))) {
            return false;
        }
        if (!((set.get(0).shading == set.get(1).shading) && (set.get(1).shading == set.get(2).shading) ||
                (set.get(0).shading != set.get(1).shading) && (set.get(0).shading != set.get(2).shading) && (set.get(1).shading != set.get(2).shading))) {
            return false;
        }
		return true;
	}
	
	/**
	 * Checks to see whether a set exists on the table, in order to know if 3 cards need to be dealt
	 * @author		ysharma1126
	 * @param	cards	cards on he table 
	 * @return		boolean representing whether there is a set on the table
	 *
	 */
	boolean checkSetexists(ArrayList <Card> cards) {
		if (cards == null) {
			return false;
		}
		int size = cards.size();
		for (int i = 0; i < size; i++){
			Card a = cards.get(i);
			for (int j = 0; j < size; j++) {
				Card b = cards.get(j);
				for (int k = 0; k < size; k++) {
					Card c = cards.get(k);
					if (validateSet(a,b,c)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * Deals the cards
	 * @author		ysharma1126
	 * @param	deck	The remaining cards in the deck
	 * @param	table	The cards currently on the table
	 * @param	numcards	Number of cards to be dealt
	 *
	 */
	void dealCards(ArrayList <Card> deck, ArrayList <Card> table, int numcards) {
		for (int i = 0; i < numcards; i++) {
			if (deck.isEmpty()) {
				break;
			}
			table.add(deck.remove(deck.size()-1));
		}
	}
	/**
	 * Removes the cards
	 * @author		ysharma1126
	 * @param	set	The set that needs to be removed
	 * @param	table	The cards currently on the table
	 *
	 */
	void removeCards(ArrayList <Card> set, ArrayList <Card> table) {
		for (Card card: set) {
			table.remove(card);
		}
	}
	/**
	 * Updates the player's setcount
	 * @author		ysharma1126
	 * @param	p	Player whose set count needs to be incremented
	 *
	 */
	void updateSetcount(Player p) {
		p.setcount++;
	}
}

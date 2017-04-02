package gamelogic;
import java.util.ArrayList;
import java.util.Arrays;
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
						cards.add(new Card(shape, number, color, shading, false));
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
	 * @param	set	card trio in question
	 * x@return		boolean representing whether the card trio is a set
	 *
	 */
	public boolean validateSet(ArrayList <Card> set) {
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
	public boolean checkSetexists(ArrayList <Card> cards) {
		if (cards == null) {
			return false;
		}
		int size = cards.size();
		boolean holecheck = false;
		for (int i = 0; i < size; i++){
			Card a = cards.get(i);
			if (a.hole) {
				holecheck = true;
			}
			for (int j = 0; j < size; j++) {
				Card b = cards.get(j);
				if (b.hole) {
					holecheck = true;
				}
				for (int k = 0; k < size; k++) {
					Card c = cards.get(k);
					if (c.hole) {
						holecheck = true;
					}
					ArrayList <Card> temp = new ArrayList <Card>();
					temp.addAll(Arrays.asList(a,b,c));
					if (validateSet(temp) && (!holecheck)) {
						return true;
					}
					temp.clear();
				}
			}
		}
		return false;
	}
	/**
	 * Init table
	 * @author		ysharma1126
	 * @param	deck	The remaining cards in the deck
	 * @param	table	The cards currently on the table
	 * @param	numcards	Number of cards to be dealt
	 *
	 */
	public void initTable(ArrayList <Card> deck, ArrayList <Card> table, int numcards) {
		for (int i = 0; i < numcards; i++) {
			if (deck.isEmpty()) {
				break;
			}
			table.add(deck.remove(deck.size()-1));
		}
	}
	
	/**
	 * Deal cards
	 * @author		ysharma1126
	 * @param	deck	The remaining cards in the deck
	 * @param	table	The cards currently on the table
	 * @param	numcards	Number of cards to be dealt
	 *
	 */
	public void dealCards(ArrayList <Card> deck, ArrayList <Card> table, int numcards) {
		if (deck.isEmpty()) {
			return;
		}
		boolean holechecker = false;
		int count = 0;
		for (Card card: table) {
			if (count != (numcards - 1)) {
				if (card.hole) {
					holechecker = true;
					table.get(table.indexOf(card)).hole = false;
					table.set(table.indexOf(card), deck.remove(deck.size()-1));
					count++;
				}
			}
			else {
				break;
			}
		}
		if (!holechecker) {
			for (int i = 0;i < numcards;i++) {
				table.add(deck.remove(deck.size()-1));
			}
		}
	}
	
	/**
	 * Replaces the cards
	 * @author		ysharma1126
	 * @param	deck	The remaining cards in the deck
	 * @param	table	The cards currently on the table
	 * @param	numcards	Number of cards to be dealt
	 *
	 */
	public void replaceCards(ArrayList <Card> set, ArrayList <Card> deck, ArrayList <Card> table) {
		for (Card card: set) {
			if (deck.isEmpty()) {
				break;
			}
			for (Card card1: table) {
				if(card1 == card) {
					Card temp = deck.remove(deck.size()-1);
					System.out.println(temp.toImageFile());
					System.out.println(card1.toImageFile());
					table.set(table.indexOf(card1), deck.remove(deck.size()-1));
					
				}
			}
		}
	}
	/**
	 * Removes the cards
	 * @author		ysharma1126
	 * @param	set	The set that needs to be removed
	 * @param	table	The cards currently on the table
	 *
	 */
	public void removeCards(ArrayList <Card> set, ArrayList <Card> table) {
		for (Card card: set) {
			for (Card card1: table) {
				if (card == card1) {
					table.get(table.indexOf(card1)).hole = true;
				}
			}
		}
	}
	/**
	 * Updates the player's setcount
	 * @author		ysharma1126
	 * @param	p	Player whose set count needs to be incremented
	 *
	 */
	public void updateSetcount(Player p) {
		p.setcount++;
	}
	
	public int getsize(ArrayList <Card> table) {
		int size = 0;
		for (Card card: table) {
			if (!card.hole) {
				size++;
			}
		}
		System.out.println(size);
		return size;
	}
}

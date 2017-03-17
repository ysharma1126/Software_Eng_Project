package Set_Game;
import java.util.ArrayList;
/**
 * Handles actually playing the game set.
 * @author ysharma1126
 *
 */
public class Server {
	/**
	 * Plays the game. A game is initialized, the deck is created, and 12 cards are placed on the table.
	 * Then while the game is on, when the deck isn't empty or there is a set on the table, check whether there's a set on the table
	 * , if not deal 3 more cards. Wait for player to pick set, if valid, update set count, remove the 3 set cards, 
	 * and deal 3 more cards if there are less than 12 cards on the board. Once while loop is exited, the game has ended.
	 * Push game/player stats to DB, and send data to client for display
	 * @author		ysharma1126
	 * @param	p	Players who have entered this game
	 *
	 */
	public void play(ArrayList <Player> p) {
		
		Game game = new Game();
		ArrayList <Card> deck = game.createDeck();
		ArrayList <Card> table = new ArrayList <Card>();
		game.dealCards(deck, table, 12);
		while(!deck.isEmpty() || game.checkSetexists(table)) {
			if (!game.checkSetexists(table)) {
				game.dealCards(deck, table, 3);
			}
			//prompt and wait for player p to pick cardset s
			if (game.validateSet(s)) {
				game.updateSetcount(p);
				game.removeCards(s, table);
				if (table.size() < 12) {
					game.dealCards(deck, table, 3);
				}
			}
		}
		//Push/Send game/player stats to DB/client
	}

}

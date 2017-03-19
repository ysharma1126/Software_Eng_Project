package gamelogic;

/**
 * Player class, contains attributes of a player object
 * @author ysharma1126
 * 
 *
 */
public class Player {
	public String username;
	public int setcount;
	/**
	 * Initializes a player
	 * @author		ysharma1126
	 * @param	uname	Player username	
	 *
	 */
	public Player(String uname) {
		username = uname;
		setcount = 0;
	}
}

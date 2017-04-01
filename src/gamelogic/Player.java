package gamelogic;

import java.io.Serializable;

/**
 * Player class, contains attributes of a player object
 * @author ysharma1126
 * 
 *
 */
public class Player implements Serializable {
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

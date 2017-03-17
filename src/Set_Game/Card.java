package Set_Game;
/**
 * Card class, contains attributes of a card object
 * @author ysharma1126
 * 
 *
 */
public class Card {
	public int shape;
	public int number;
	public int color;
	public int shading;
	/**
	 * Initializes a card
	 * @author		ysharma1126
	 * @param	shpe	Shape feature	
	 * @param	num	Number feature
	 * @param	col	Color feature
	 * @param	shde	Shading feature
	 *
	 */
	public Card(int shpe, int num, int col, int shde) {
		shape = shpe;
		number = num;
		color = col;
		shading = shde;
	}
}

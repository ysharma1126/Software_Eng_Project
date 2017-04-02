package gamelogic;

import java.io.Serializable;

/**
 * Card class, contains attributes of a card object
 * @author ysharma1126
 * 
 *
 */
public class Card implements Serializable{
	public int shape;
	public int number;
	public int color;
	public int shading;
	public boolean hole;
	/**
	 * Initializes a card
	 * @author		ysharma1126
	 * @param	shpe	Shape feature	
	 * @param	num	Number feature
	 * @param	col	Color feature
	 * @param	shde	Shading feature
	 *
	 */
	
	/*
	 * Cards get translated to:
	 * fill, shape, color, num, 
	 */
	
	public Card(int shpe, int num, int col, int shde, boolean hle) {
		shape = shpe;
		number = num;
		color = col;
		shading = shde;
		hole = hle;
	}
	
	public String toImageFile()
	{
	  if(!this.hole) {
		  String image = "";
		  image = image + this.shading + this.shape + this.color + this.number + ".png";
		  return image;
	  }
	  else {
		  return("hole");
	  }
	}
	
	
	@Override
	public String toString()
	{
	  String return_string = "Shape: " + shape + "Number: " + number + "Color: " + color + "Shading: " + shading;
	  return return_string;
	}
}

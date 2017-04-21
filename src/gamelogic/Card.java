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
	public double randomnum;
	public int hole_test;
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
		hole_test = 0;
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
	
	public String getDescription(){
		String description = "";
		switch(shape){
			case 0:
				description += "Wave";
				break;
			case 1:
				description += "Diamond";
				break;
			case 2:
				description += "Oval";
				break;
		}
		description += "\t";
		switch(color){
		case 0:
			description += "Red";
			break;
		case 1:
			description += "Purple";
			break;
		case 2:
			description += "Green";
			break;
		}
		description += "\t";
		switch(shading){
		case 0:
			description += "Solid";
			break;
		case 1:
			description += "Wavy";
			break;
		case 2:
			description += "Empty";
			break;
		}
		description += "\t";
		switch(number){
		case 0:
			description += "One";
			break;
		case 1:
			description += "Two";
			break;
		case 2:
			description += "Three";
			break;
		}
		description += "\t";
		if(hole) {
			description += "hole";
		}
		else {
			description += "nothole";
		}
		description += "\t";
		description += "hole_test: " + hole_test;
		return description;
	}
	
	
	@Override
	public String toString()
	{
	  String return_string = "Shape: " + shape + "Number: " + number + "Color: " + color + "Shading: " + shading;
	  return return_string;
	}
}

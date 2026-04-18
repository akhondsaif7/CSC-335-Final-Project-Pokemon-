package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class WildboltStorm extends Move implements Serializable{
	public WildboltStorm() {
		super("Wildbolt Storm", Type.ELECTRIC, 10, 100, Category.SPECIAL); // 100 Power and 10 PP for Wildbolt Storm
	}

}
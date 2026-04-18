package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class Earthquake extends Move implements Serializable{
	public Earthquake() {
		super("Earthquake", Type.GROUND, 8, 100, Category.PHYSICAL); // 100 Power and 8 PP for Earthquake
	}
}
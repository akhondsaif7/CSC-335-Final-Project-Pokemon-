package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class Waterfall extends Move implements Serializable{
	public Waterfall() {
		super("Waterfall", Type.WATER, 15, 80, Category.PHYSICAL); // 80 Power and 15 PP for Waterfall
	}
}
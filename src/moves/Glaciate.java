package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class Glaciate extends Move implements Serializable{
	public Glaciate() {
		super("Glaciate", Type.ICE, 10, 65, Category.SPECIAL); // 65 Power and 10 PP for Facade
	}
}
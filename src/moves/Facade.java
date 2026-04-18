package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class Facade extends Move implements Serializable{
	public Facade() {
		super("Facade", Type.NORMAL, 20, 70, Category.PHYSICAL); // 70 Power and 20 PP for Facade
	}
}
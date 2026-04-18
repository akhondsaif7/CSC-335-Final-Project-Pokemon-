package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class Flamethrower extends Move implements Serializable{
	public Flamethrower() {
		super("Flamethrower", Type.FIRE, 15, 90, Category.SPECIAL); // 90 Power and 15 PP for Flamethrower
	}

}
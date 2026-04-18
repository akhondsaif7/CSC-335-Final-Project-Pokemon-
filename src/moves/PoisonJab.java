package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class PoisonJab extends Move implements Serializable{
	public PoisonJab() {
		super("Poison Jab", Type.POISON, 20, 80, Category.PHYSICAL); // 80 Power and 20 PP for Poison Jab
	}

}
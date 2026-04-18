package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class StoneEdge extends Move implements Serializable{
	public StoneEdge() {
		super("Stone Edge", Type.ROCK, 5, 100, Category.PHYSICAL); // 100 Power and 5 PP for Stone Edge
	}
}
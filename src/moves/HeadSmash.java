package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class HeadSmash extends Move implements Serializable{
	public HeadSmash() {
		super("Head Smash", Type.ROCK, 5, 150, Category.SPECIAL); // 150 Power and 5 PP for Head Smash
	}
}
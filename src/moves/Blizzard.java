package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Type;

public class Blizzard extends Move implements Serializable{
	public Blizzard() {
		super("Blizzard", Type.ICE, 5, 110, Category.SPECIAL);
	}
}
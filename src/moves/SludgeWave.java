package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class SludgeWave extends Move implements Serializable{
	public SludgeWave() {
		super("Sludge Wave", Type.POISON, 10, 95, Category.SPECIAL); // 95 Power and 10 PP for Sludge Wave
	}

}
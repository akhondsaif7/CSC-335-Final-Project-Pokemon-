package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class Covet extends Move implements Serializable{
	public Covet() {
		super("Covet", Type.NORMAL, 25, 60, Category.PHYSICAL);
	}
}
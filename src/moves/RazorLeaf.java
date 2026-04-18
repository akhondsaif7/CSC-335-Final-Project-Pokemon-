package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class RazorLeaf extends Move implements Serializable{
	public RazorLeaf() {
		super("Razor Leaf", Type.GRASS, 25, 55, Category.PHYSICAL);
	}
}
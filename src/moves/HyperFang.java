package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class HyperFang extends Move implements Serializable{
	public HyperFang() {
		super("Hyperfang", Type.NORMAL, 15, 80, Category.SPECIAL); // 80 Power and 15 PP for Hyper Fang
	}

}
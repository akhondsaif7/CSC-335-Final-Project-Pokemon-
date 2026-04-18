package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class FireFang extends Move implements Serializable{
	public FireFang() {
		super("Fire Fang", Type.FIRE, 15, 65, Category.PHYSICAL); // 65 Power and 15 PP for Fire Fang
	}

}
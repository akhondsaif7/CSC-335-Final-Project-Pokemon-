package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class IcePunch extends Move implements Serializable{
	public IcePunch() {
		super("Ice Punch", Type.ICE, 15, 75, Category.PHYSICAL); // 75 Power and 15 PP for Ice Punch
	}
}
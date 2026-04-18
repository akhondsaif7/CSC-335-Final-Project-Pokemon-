package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class DracoMeteor extends Move implements Serializable{
	public DracoMeteor() {
		super("Draco Meteor", Type.DRAGON, 5, 130, Category.SPECIAL); // 130 Power and 5 PP for Draco Meteor
	}

}
package moves;

import java.io.Serializable;

import model.Move;
import model.Move.Category;
import model.Move.Type;

public class VineWhip extends Move implements Serializable{
    public VineWhip() {
        super("Vine whip", Type.GRASS, 25, 45, Category.PHYSICAL); // 25 PP and 45 Power for Vine Whip
    }
}
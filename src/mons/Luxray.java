package mons;

import model.Mon;
import model.Move.Type;
import moves.*;
public class Luxray extends Mon{
	public Luxray() {
		this.name = "Luxray";
		this.type = Type.ELECTRIC;
		this.maxHp = 80;
		this.currHp = maxHp;
		this.attack = 120;
		this.specialAttack = 95;
		this.specialDefence = 79;
		this.defence = 79;
		this.spritePlayer = "/media/battle/luxrayPlayer.png";
		this.spriteOpp = "/media/battle/luxrayOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new WildboltStorm();
		this.moves[1] = new HyperFang();
		this.moves[2] = new FireFang();
		this.moves[3] = new Covet();
	}
}
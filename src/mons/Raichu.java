package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Raichu extends Mon{
	public Raichu() {
		this.name = "Raichu";
		this.type = Type.ELECTRIC;
		this.maxHp = 60;
		this.currHp = maxHp;
		this.attack = 90;
		this.specialAttack = 90;
		this.specialDefence = 80;
		this.defence = 55;
		this.spritePlayer = "/media/battle/raichuPlayer.png";
		this.spriteOpp = "/media/battle/raichuOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new WildboltStorm();
		this.moves[1] = new HyperFang();
		this.moves[2] = new Facade();
		this.moves[3] = new VineWhip();
	}
}
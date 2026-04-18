package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Feraligatr extends Mon{
	public Feraligatr() {
		this.name = "Feraligatr";
		this.type = Type.WATER;
		this.maxHp = 85;
		this.currHp = maxHp;
		this.attack = 105;
		this.specialAttack = 79;
		this.specialDefence = 83;
		this.defence = 100;
		this.spritePlayer = "/media/battle/feraligatrPlayer.png";
		this.spriteOpp = "/media/battle/feraligatrOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Waterfall();
		this.moves[1] = new IcePunch();
		this.moves[2] = new FireFang();
		this.moves[3] = new Facade();
	}
}
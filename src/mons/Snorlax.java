package mons;

import model.Mon;
import model.Move.Type;
import moves.*;
public class Snorlax extends Mon{
	public Snorlax() {
		this.name = "Snorlax";
		this.type = Type.NORMAL;
		this.maxHp = 160;
		this.currHp = maxHp;
		this.attack = 110;
		this.specialAttack = 65;
		this.specialDefence = 110;
		this.defence = 65;
		this.spritePlayer = "/media/battle/snorlaxPlayer.png";
		this.spriteOpp = "/media/battle/snorlaxOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Facade();
		this.moves[1] = new Earthquake();
		this.moves[2] = new Covet();
		this.moves[3] = new IcePunch();
	}
}
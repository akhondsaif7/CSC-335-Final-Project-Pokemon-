package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Breloom extends Mon{
	public Breloom() {
		this.name = "Breloom";
		this.type = Type.GRASS;
		this.maxHp = 60;
		this.currHp = maxHp;
		this.attack = 130;
		this.specialAttack = 60;
		this.specialDefence = 60;
		this.defence = 80;
		this.spritePlayer = "/media/battle/breloomPlayer.png";
		this.spriteOpp = "/media/battle/breloomOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new RazorLeaf();
		this.moves[1] = new IcePunch();
		this.moves[2] = new PoisonJab();
		this.moves[3] = new Facade();
	}
}
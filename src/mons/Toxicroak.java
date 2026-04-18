package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Toxicroak extends Mon{
	public Toxicroak() {
		this.name = "Toxicroak";
		this.type = Type.POISON;
		this.maxHp = 83;
		this.currHp = maxHp;
		this.attack = 106;
		this.specialAttack = 86;
		this.specialDefence = 65;
		this.defence = 65;
		this.spritePlayer = "/media/battle/toxicroakPlayer.png";
		this.spriteOpp = "/media/battle/toxicroakOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Facade();
		this.moves[1] = new FireFang();
		this.moves[2] = new PoisonJab();
		this.moves[3] = new RazorLeaf();
	}
}
package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Swampert extends Mon{
	public Swampert() {
		this.name = "Swampert";
		this.type = Type.WATER;
		this.maxHp = 100;
		this.currHp = maxHp;
		this.attack = 110;
		this.specialAttack = 85;
		this.specialDefence = 90;
		this.defence = 90;
		this.spritePlayer = "/media/battle/swampertPlayer.png";
		this.spriteOpp = "/media/battle/swampertOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Waterfall();
		this.moves[1] = new Earthquake();
		this.moves[2] = new Facade();
		this.moves[3] = new Glaciate();
	}
}
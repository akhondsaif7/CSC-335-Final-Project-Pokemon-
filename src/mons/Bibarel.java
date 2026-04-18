package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Bibarel extends Mon{
	public Bibarel() {
		this.name = "Bibarel";
		this.type = Type.NORMAL;
		this.maxHp = 79;
		this.currHp = maxHp;
		this.attack = 85;
		this.specialAttack = 55;
		this.specialDefence = 60;
		this.defence = 60;
		this.spritePlayer = "/media/battle/bibarelPlayer.png";
		this.spriteOpp = "/media/battle/bibarelOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Facade();
		this.moves[1] = new StoneEdge();
		this.moves[2] = new Covet();
		this.moves[3] = new Waterfall();
	}
}
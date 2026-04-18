package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Lapras extends Mon{
	public Lapras() {
		this.name = "Lapras";
		this.type = Type.ICE;
		this.maxHp = 130;
		this.currHp = this.maxHp;
		this.attack = 85;
		this.specialAttack = 85;
		this.specialDefence = 95;
		this.defence = 80;
		this.spritePlayer = "/media/battle/laprasPlayer.png";
		this.spriteOpp = "/media/battle/laprasOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Blizzard();
		this.moves[1] = new Glaciate();
		this.moves[2] = new Covet();
		this.moves[3] = new Waterfall();
	}
}
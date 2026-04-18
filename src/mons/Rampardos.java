package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Rampardos extends Mon{
	public Rampardos() {
		this.name = "Rampardos";
		this.type = Type.ROCK;
		this.maxHp = 97;
		this.currHp = maxHp;
		this.attack = 165;
		this.specialAttack = 65;
		this.specialDefence = 50;
		this.defence = 60;
		this.spritePlayer = "/media/battle/rampardosPlayer.png";
		this.spriteOpp = "/media/battle/rampardosOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new HeadSmash();
		this.moves[1] = new Earthquake();
		this.moves[2] = new Covet();
		this.moves[3] = new Waterfall();
	}
}
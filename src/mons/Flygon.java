package mons;

import model.Mon;
import model.Move.Type;
import moves.*;
public class Flygon extends Mon{
	public Flygon() {
		this.name = "Flygon";
		this.type = Type.GROUND;
		this.maxHp = 80;
		this.currHp = maxHp;
		this.attack = 100;
		this.specialAttack = 80;
		this.specialDefence = 80;
		this.defence = 80;
		this.spritePlayer = "/media/battle/flygonPlayer.png";
		this.spriteOpp = "/media/battle/flygonOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Earthquake();
		this.moves[1] = new DracoMeteor();
		this.moves[2] = new StoneEdge();
		this.moves[3] = new Flamethrower();
	}
}
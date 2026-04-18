package mons;

import model.Mon;
import model.Move.Type;
import moves.*;
public class Typhlosion extends Mon{
	public Typhlosion() {
		this.name = "Typhlosion";
		this.type = Type.FIRE;
		this.maxHp = 78;
		this.currHp = maxHp;
		this.attack = 84;
		this.specialAttack = 109;
		this.specialDefence = 85;
		this.defence = 78;
		this.spritePlayer = "/media/battle/typhlosionPlayer.png";
		this.spriteOpp = "/media/battle/typhlosionOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Flamethrower();
		this.moves[1] = new FireFang();
		this.moves[2] = new Earthquake();
		this.moves[3] = new HyperFang();
	}
}
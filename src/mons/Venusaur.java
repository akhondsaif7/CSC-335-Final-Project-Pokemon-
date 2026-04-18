package mons;

import model.Mon;
import model.Move.Type;
import moves.*;
public class Venusaur extends Mon{
	public Venusaur() {
		this.name = "Venusaur";
		this.type = Type.GRASS;
		this.maxHp = 80;
		this.currHp = maxHp;
		this.attack = 82;
		this.specialAttack = 100;
		this.specialDefence = 100;
		this.defence = 83;
		this.spritePlayer = "/media/battle/venusaurPlayer.png";
		this.spriteOpp = "/media/battle/venusaurOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new VineWhip();
		this.moves[1] = new HyperFang();
		this.moves[2] = new SludgeWave();
		this.moves[3] = new RazorLeaf();
	}
}
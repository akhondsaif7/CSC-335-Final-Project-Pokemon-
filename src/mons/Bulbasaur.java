package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Bulbasaur extends Mon{
	public Bulbasaur() {
		this.name = "Bulbasaur";
		this.type = Type.GRASS;
		this.maxHp = 45;
		this.currHp = maxHp;
		this.attack = 49;
		this.specialAttack = 65;
		this.specialDefence = 65;
		this.defence = 49;
		this.spritePlayer = "/media/battle/bulbasaurPlayer.png";
		this.spriteOpp = "/media/battle/bulbasaurOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new VineWhip();
		this.moves[1] = new HyperFang();
		this.moves[2] = new Covet();
		this.moves[3] = new RazorLeaf();
	}
}
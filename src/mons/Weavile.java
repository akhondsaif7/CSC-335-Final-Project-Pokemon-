package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Weavile extends Mon{
	public Weavile() {
		this.name = "Weavile";
		this.type = Type.ICE;
		this.maxHp = 70;
		this.currHp = maxHp;
		this.attack = 120;
		this.specialAttack = 45;
		this.specialDefence = 85;
		this.defence = 65;
		this.spritePlayer = "/media/battle/weavilePlayer.png";
		this.spriteOpp = "/media/battle/weavileOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new IcePunch();
		this.moves[1] = new Facade();
		this.moves[2] = new Covet();
		this.moves[3] = new RazorLeaf();
	}
}
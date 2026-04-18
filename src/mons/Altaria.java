package mons;

import model.Mon;
import model.Move.Type;
import moves.*;

public class Altaria extends Mon{
	public Altaria() {
		this.name = "Altaria";
		this.type = Type.DRAGON;
		this.maxHp = 75;
		this.currHp = maxHp;
		this.attack = 70;
		this.specialAttack = 70;
		this.specialDefence = 105;
		this.defence = 90;
		this.spritePlayer = "/media/battle/altariaPlayer.png";
		this.spriteOpp = "/media/battle/altariaOpp.png";
		buildMoveset();
	}

	public void buildMoveset() {
		this.moves[0] = new Blizzard();
		this.moves[1] = new DracoMeteor();
		this.moves[2] = new Earthquake();
		this.moves[3] = new WildboltStorm();
	}
}
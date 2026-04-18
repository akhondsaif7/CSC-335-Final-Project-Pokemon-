package model;

import java.io.Serializable;

/**
 * Abstract class that all mons should inherit from.
 * Defines the basic parameters of what makes up a mon. Mons have 4 {@link Move moves}, a type (can be changed later when relevant) and HP.
 * {@link Move} usage saved by the {@link Move} object itself.
 * @author Isa Abdulrazaq
 */
public abstract class Mon implements Serializable {
	private static final long serialVersionUID = 1407793300997629230L;
	/**
	 * The type associated with the pokemon.
	 * Relevant when type advantages will be implemented.
	 */
	protected String name;
	protected Move.Type type;
	protected Move[] moves = new Move[4];
	protected int maxHp;
	protected int currHp;
	protected int attack;
	protected int specialAttack;
	protected int defence;
	protected int specialDefence;
	protected String spritePlayer;
	protected String spriteOpp;
	/**
	 * Return's the Mon's attack
	 * @return {@link #attack}
	 */
	public int getAtt() {return attack;}
	/**
	 * Return's the Mon's specialAttack
	 * @return {@link #specialAttack}
	 */
	public int getSAtt() {return specialAttack;}
	/**
	 * Return's the Mon's specialDefence
	 * @return {@link #specialDefence}
	 */
	public int getSDef() {return specialDefence;}
	/**
	 * Return's the Mon's defence
	 * @return {@link #defence}
	 */
	public int getDef() {return defence;}
	/**
	 * Return's the Mon's name
	 * @return {@link #name}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Return's the Mon's max health
	 * @return {@link #maxHp} the maximum health the Mon can have
	 */
	public int getMaxHp() {
		return maxHp;
	}
	
	/**
	 * Returns the Mon's current health
	 * @return {@link #currHp} the Mon's current health
	 */
	public int getCurrHp() {
		return currHp;
	}
	
	/**
	 * Returns a reference to the {@link Move} object at the provided index of the {@link #moves} array.
	 * @param index 0-3 for which of the Mon's moves are to be returned
	 * @return {@link Move} at the given index
	 */
	public Move getMove(int index) {
		return moves[index];
	}
	
	/**
	 * Returns a reference to the entire move array. Useful for views to display all of the Mon's moves
	 * @return {@link #moves}
	 */
	public Move[] getAllMoves() {
		return moves;
	}
	
	/**
	 * Returns the Mon's type. Not relevant until type advantages are implemented
	 * @return {@link #type}
	 */
	public Move.Type getType() {
		return type;
	}

	/**
	 * Reduces the Mon's currHp by a given amount. To be called by {@link Move#applyEffect(Mon, Mon)}.
	 * @param damage how much health the Mon should lose
	 */
	public void takeDamage(int damage, Mon attacker, Move.Category category) {
		
		if (category == Move.Category.PHYSICAL) 
		{
			currHp -= (damage * attacker.getAtt()) / getDef() / 2;
		}
		else if (category == Move.Category.SPECIAL) 
		{
			currHp -= (damage * attacker.getSAtt()) / getSDef() / 2;
		}
		
		if (currHp < 0) currHp = 0; // prevent negative health
		
	}
	
	/**
	 * Determines whether the Mon has been defeated or if they can continue battling.
	 * @return true/false depending on whether their health has reached 0 or not.
	 */
	public boolean hasFainted() {
		return currHp == 0;
	}
	/**
	 * Heals pokemon by int healing amount
	 * @param int healing
	 */
	public void heal(int healing) 
	{
		currHp += healing;
		if (currHp > maxHp) 
		{
			currHp = maxHp;
		}
	}
	
	/**
	 * Returns a String of the player view sprite for the current Mon. Sprites pulled from: https://pokemondb.net/sprites/bulbasaur, you can search
	 * XXXX sprites (where XXXX is the name of the pokemon) to find them. Lapras and Bulbasaur use Gen 4 sprites.
	 * @return String of the file path to sprite.
	 */
	public String getPlayerSprite() {
		return spritePlayer;
	}
	
	/**
	 * Returns a String of the opponent view sprite for the current Mon. Sprites pulled from: https://pokemondb.net/sprites/bulbasaur
	 * @return String of the file path to sprite.
	 */
	public String getOppSprite() {
		return spriteOpp;
	}
}
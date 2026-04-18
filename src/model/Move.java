package model;

import java.io.Serializable;

/**
 * Abstract class that all {@link Mon} moves should inherit from. Defines the
 * basic requirements for what a move should do, but leaves its effect to be
 * implemented by the move itself.
 * 
 * @author Isa Abdulrazaq
 */
public abstract class Move implements Serializable {
	private static final long serialVersionUID = 4547556124460762590L;

	/**
	 * Physical Special Split Moves
	 */
	public enum Category {
		PHYSICAL, SPECIAL
	}

	Category category;
	int maxPP;
	String name;

	public Move(String name, Type type, int PP, int power, Category category) {
		this.name = name;
		this.type = type;
		this.category = category;
		this.maxPP = PP;
		this.PP = PP;
		this.power = power;
	}

	/**
	 * Getter for name
	 * 
	 * @return String name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Enum to support type advantage calculations later in development. Currently
	 * only has one type.
	 */
	public enum Type {
		NORMAL, ICE, WATER, GRASS, POISON, FIRE, ELECTRIC, GROUND, ROCK, DRAGON
	}

	private Type type;

	/**
	 * Getter for type
	 * 
	 * @return Type type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * How many uses of the move are remaining
	 */
	private int PP;

	private int power;

	/**
	 * Getter for power
	 * 
	 * @return int power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * Abstract method that must be implemented by all subclassing moves. Defines
	 * what the move actually does and how it changes the game state
	 * 
	 * @param user  the {@link Mon} using the move. Useful if the move buffs the
	 *              user in any way.
	 * @param enemy the {@link Mon} battling the move's user. Will call
	 *              {@link Mon#takeDamage(int, Mon)} if the move is a damaging move.
	 */
	public void applyEffect(Mon user, Mon enemy) {

		// STAB (same-type attack bonus)
		double stab = (user.getType() == this.getType()) ? 1.5 : 1.0;

		// Type advantage multiplier
		double effectiveness = TypeEffectiveness.getMultiplier(this.getType(), enemy.getType());

		// effective power after multipliers
		int adjustedPower = (int) (this.getPower() * stab * effectiveness);

		// deals damage via Mon stats
		enemy.takeDamage(adjustedPower, user, this.category);

		updatePP();
	}

	/**
	 * Used to ensure that a move isn't usable if it no longer has PP
	 * 
	 * @return {@link #PP} the number of uses remaining for the move
	 */
	public int getPP() {
		return PP;
	}

	/**
	 * Returns the maxPP value for the UI
	 * 
	 * @return {@link #maxPP} The total number of times the move can be used.
	 */
	public int getMaxPP() {
		return maxPP;
	}

	/**
	 * Sets PP to maxPP
	 */
	public void refillPP() {
		PP = maxPP;
	}

	/**
	 * Updates PP by deducting PP by 1
	 */
	public void updatePP() {
		PP--;
	}

}
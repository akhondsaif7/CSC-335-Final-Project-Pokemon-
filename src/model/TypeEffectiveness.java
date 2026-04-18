package model;

import model.Move.Type;

/**
 * Provides the type effectiveness multipliers between belligerent Pokémon types.
 *
 * This class is used to calculate damage multipliers for any move based on the
 * attacker's type and the defender's type. Multipliers follow standard Pokémon
 * rules.
 * 
 * @author Akhond Saif Al Masud
 */
public class TypeEffectiveness {

	/**
	 * Returns the type effectiveness multiplier of an attacking move against a
	 * defending Pokémon type.
	 *
	 * @param atk The type of the attacking move.
	 * @param def The type of the defending Pokémon.
	 * @return A double representing the effectiveness multiplier: 2.0 = super
	 *         effective, 0.5 = not very effective, 0.0 = no effect, 1.0 = normally
	 *         effective.
	 */
	public static double getMultiplier(Type atk, Type def) {

		return switch (atk) {

		case NORMAL -> switch (def) {
		case ROCK -> 0.5;
		default -> 1.0;
		};
		case ICE -> switch (def) {
		case GRASS, DRAGON, GROUND -> 2.0;
		case WATER, ICE, FIRE -> 0.5;
		default -> 1.0;
		};

		case WATER -> switch (def) {
		case FIRE, ROCK, GROUND -> 2.0;
		case WATER, GRASS, DRAGON -> 0.5;
		default -> 1.0;
		};

		case GRASS -> switch (def) {
		case WATER, ROCK, GROUND -> 2.0;
		case FIRE, GRASS, POISON, DRAGON -> 0.5;
		default -> 1.0;
		};

		case POISON -> switch (def) {
		case GRASS -> 2.0;
		case POISON, GROUND, ROCK -> 0.5;
		default -> 1.0;
		};

		case FIRE -> switch (def) {
		case GRASS, ICE -> 2.0;
		case FIRE, WATER, ROCK, DRAGON -> 0.5;
		default -> 1.0;
		};

		case ELECTRIC -> switch (def) {
		case WATER -> 2.0;
		case GRASS, DRAGON, ELECTRIC -> 0.5;
		case GROUND -> 0.0; // immunity
		default -> 1.0;
		};

		case GROUND -> switch (def) {
		case FIRE, ELECTRIC, POISON, ROCK -> 2.0;
		case GRASS -> 0.5;
		default -> 1.0;
		};

		case ROCK -> switch (def) {
		case FIRE, ICE -> 2.0;
		case GROUND -> 0.5;
		default -> 1.0;
		};

		case DRAGON -> switch (def) {
		case DRAGON -> 2.0;
		default -> 1.0;
		};
		};
	}
}

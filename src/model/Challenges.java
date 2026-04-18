package model;
/**
 * Challenges is a storage object for the levels system. Stores the enemy, it's unlocked state,
 * and it's name. Also is able to get Mons on enemy team
 * @author Bradley Adams
 */
public class Challenges {
	private String name;
	private Player enemy;
	private boolean unlocked = false;
	public Challenges(String name, Player enemy) 
	{
		this.name = name;
		this.enemy = enemy;
	}
	/**
	 * Getter for Player object enemy
	 * @return enemy Player object
	 */
	public Player getEnemy() 
	{
		return enemy;
	}
	/**
	 * Getter for challenge name
	 * @return name String
	 */
	public String getName() 
	{
		return name;
	}
	/**
	 * Getter for enemies Mon at i index
	 * @param int i for index
	 * @return Mon object from enemy team at index i
	 */
	public Mon getPokemonAt(int i) 
	{
		return enemy.getMon(i);
	}
	/**
	 * Unlocks challenge, switching unlock boolean to true
	 * @return true if wasn't unlocked yet, false if already unlocked
	 */
	public boolean unlock() 
	{
		if (unlocked) 
		{
			return false;
		}
		unlocked = true;
		return true;
	}
	/**
	 * Getter for if Challenges is locked
	 * @return boolean negation of unlocked
	 */
	public boolean isLocked() 
	{
		return !unlocked;
	}
	/**
	 * Resets the enemys pokemon by healing them all and giving
	 * them max PP on all moves
	 */
	public void resetEnemy() 
	{
		enemy.resetMons();
	}
	
	
}
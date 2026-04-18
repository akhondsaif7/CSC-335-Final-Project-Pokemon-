package model;

import java.io.Serializable;

/**
 * A class representing one of the two players in a given fight.
 * Holds references to the player's full team as well as the current active {@link Mon}.
 * @author Isa Abdulrazaq
 */
public class Player implements Serializable {
	private static final long serialVersionUID = 5355143888361177700L;
	/**
	 * The Player's full team. Max size 6.
	 */
	private Mon[] mons = new Mon[6];
	private Mon[] pokedex = new Mon[32];
	private Mon activeMon;
	
	/**
	 * Constructs a new Player object with the team of {@link Mon}'s passed as a param
	 * @param monArray a team of {@link Mon}'s (max 6, min 1) associated with the player
	 * @throws IllegalArgumentException if the team size is incorrect.
	 */
	public Player(Mon[] monArray) throws IllegalArgumentException {
		if (0 >= monArray.length || monArray.length > 6) {
			throw new IllegalArgumentException("Incorrect team size!"); // shouldn't happen since monArray is defined by the program and not the user.
		}
		else {
			mons = monArray;
			activeMon = mons[0];
			for (int i = 0; i < teamSize(); i++)
			{
				pokedex[i] = mons[i];
			}
		}
	}
	/**
	 * Adds a Mon to Dex list, only if Mon is not already
	 * in Dex
	 * @param Mon monToAdd
	 */
	public void addToDex(Mon monToAdd) 
	{
		for (int i = 0; i < pokedexSize(); i++)
		{
			if (pokedex[i] == monToAdd)
				return;
		}
		pokedex[pokedexSize()] = monToAdd;
	}
	/**
	 * Checks if a Mon is in Dex list
	 * @param Mon monToCheck
	 * @return true if Mon is in Dex, false otherwise
	 */
	public boolean isInDex(Mon monToCheck) 
	{
		for (int i = 0; i < pokedexSize(); i++)
		{
			if (pokedex[i] == monToCheck)
				return true;
		}
		return false;
	}
	/**
	 * Getter for team
	 * @return Mon[] teamList
	 */
	public Mon[] getTeam() 
	{
		return mons;
	}
	/**
	 * Getter for size of Team
	 * @return int mons.length
	 */
	public int teamSize() {
		return mons.length;
	}
	/**
	 * Getter Dexlist size
	 * @return int size of Dex
	 */
	public int pokedexSize() 
	{
		int i = 0;
		while (pokedex[i] != null) 
		{
			i++;
		}
		return i;
	}
	/**
	 * Replaces mon of team at index i with Mon mon.
	 * @param int i for index, Mon mon for new Mon
	 */
	public void replaceMon(int i, Mon mon) 
	{
		if (i < teamSize()) 
		{
			mons[i] = mon;
		}
		else 
		{
			Mon newMons[] = new Mon[teamSize() + 1];
			for (int j = 0; j < teamSize(); j++) 
			{
				newMons[j] = mons[j];
			}
			newMons[teamSize()] = mon;
			mons = newMons;
		}
	}
	/**
	 * Check to see if Mon mon is in Team
	 * @param Mon mon
	 * @return true if Mon mon is in Team, false otherwise
	 */
	public boolean monIsInTeam(Mon mon) 
	{
		for (int i = 0; i < teamSize(); i++) 
		{
			if (mons[i] == mon) 
			{
				return true;
			}
		}
		return false;
	}
	/**
	 * Getter for mon in Dex at int index
	 * @param int i for index
	 * @return Mon mon in pokedex[i]
	 */
	public Mon getPokedexAt(int i) 
	{
		return pokedex[i];
	}
	/**
	 * Getter for mon in Team at int index
	 * @param int i for index
	 * @return Mon mon in team[i]
	 */
	public Mon getMonAt(int i) 
	{
		return mons[i];
	}
	/**
	 * Resets all mons by healing them and resetting PP on Moves
	 */
	public void resetMons() 
	{
		healAllMons();
		for (int i = 0; i < teamSize(); i++) 
		{
			Move[] moves = getMon(i).getAllMoves();
			for (int j = 0; j < moves.length; j++) 
			{
				moves[j].refillPP();
			}
		}
		switchMon(0);
	}
	/**
	 * Heals all mons on Player's Team
	 */
	public void healAllMons() 
	{
		for (int i = 0; i < teamSize(); i++) 
		{
			Mon mon = getMonAt(i);
			mon.heal(mon.getMaxHp());
		}
	}
	
	/**
	 * Returns a reference to the current Mon in combat
	 * @return {@link #activeMon} the current Mon in combat
	 */
	public Mon getActiveMon() {
		return activeMon;
	}
	
	/**
	 * Returns a {@link Mon} at the given index. Used for the swap menu.
	 * @param index Index of the {@link Mon} to return in the players team
	 * @return {@link Mon} at the index 
	 */
	public Mon getMon(int index) {
		return mons[index];
	}

	/**
	 * Changes the {@link #activeMon} to the one at the given index in {@link #mons}.
	 * @param index the index of {@link Mon} in {@link #mons} to be switched to active combat.
	 * @throws IllegalArgumentException if the {@link Mon} pointed at by index has fainted or {@link #mons}.length &lt; index.
	 */
	public void switchMon(int index) throws IllegalArgumentException {
		if (index >= mons.length || mons[index] == null ) {
			throw new IllegalArgumentException("Mon at " + index + " does not exist!");
		}
		else if (mons[index].hasFainted()) {
			throw new IllegalArgumentException("Mon at " + index + " cannot fight!");
		}
		else {
			Mon temp = mons[0];
			mons[0] = mons[index];
			mons[index] = temp;
			activeMon = mons[0];
			//activeMon = mons[index];
		}
	}
	/**
	 * Swaps Mons on team, swaps index i and j
	 * @param int i, int j
	 */
	public void swapMon(int i, int j)
	{
			Mon temp = mons[j];
			mons[j] = mons[i];
			mons[i] = temp;
			activeMon = mons[0];
	}
	
	/**
	 * Counts the number of {@link Mon}s still able to be switched to/battle with.
	 * @return count the number of {@link Mon}s that have not fainted.
	 */
	public int livingMonCount() {
		int count = 0;
		for (int i = 0; i < mons.length; i++) {
			if (!(mons[i] == null || mons[i].hasFainted())) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Determines if the player has lost the battle
	 * Players lose when they no longer have any living {@link Mon}s
	 * @return true/false depending on whether {@link #livingMonCount()} == 0.
	 */
	public boolean hasLost() {
		return livingMonCount() == 0;
	}
	
	
}
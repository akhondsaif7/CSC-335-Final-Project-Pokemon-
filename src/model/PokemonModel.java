package model;

import mons.*;

public class PokemonModel {
	private Player player;
	private ChallengesHolder challenges;
	private boolean timeToUnlock;
	private Mon unlockTeam[];
	
	public PokemonModel() 
	{
		//Starting Mon(s)
		Mon[] mons = {new Raichu()};
		player = new Player(mons);
		player.addToDex(new Snorlax());
		challenges = new ChallengesHolder();
		timeToUnlock = false;
	}
	public boolean isTimeToUnlock() 
	{
		return timeToUnlock;
	}
	public void unlocked() 
	{
		timeToUnlock = false;
	}
	public Mon[] unlockTeam() 
	{
		return unlockTeam;
	}
	
	public Player getPlayer() 
	{
		return player;
	}
	public ChallengesHolder getChallenges() 
	{
		return challenges;
	}
	public void beatLevel(Challenges selectedChallenge) 
	{
		if (challenges.beatLevel(selectedChallenge)) 
		{
			unlockTeam = selectedChallenge.getEnemy().getTeam();
			timeToUnlock = true;
		}
		
	}
	
	
}
package model;
import mons.*;
/**
 * ChallengesHolder is a storage object for all Challenges, as well as the creator of them.
 * @author Bradley Adams
 */
public class ChallengesHolder {
	private int levelCount = 7;
	private Player p1;
	private Player p2;
	private Player p3;
	private Player p4;
	private Player p5;
	private Player p6;
	private Player p7;
	private Player[] playerList = {p1, p2, p3, p4, p5, p6, p7};
	private Challenges[] challengesList = new Challenges[levelCount];
	public ChallengesHolder() 
	{
		makePlayers(makeTeams());
		makeChallenges();
	}
	/**
	 * Create any teams for Mons here for challenges
	 * @return Mon[][], a list of lists of Mon for teams of Mons
	 */
	private Mon[][] makeTeams()
	{
		Mon[] team1 = {new Bibarel()};
		Mon[] team2 = {new Lapras(), new Breloom()};
		Mon[] team3 = {new Venusaur(), new Luxray(), new Snorlax()};
		Mon[] team4 = {new Swampert(), new Altaria(), new Flygon(), new Raichu()};
		Mon[] team5 = {new Typhlosion(), new Toxicroak(), new Feraligatr(), new Weavile(), new Altaria()};
		Mon[] team6 = {new Rampardos(), new Lapras(), new Snorlax(), new Flygon(), new Weavile(), new Bibarel()};
		Mon[] team7 = {new Rampardos(), new Lapras(), new Snorlax(), new Flygon(), new Weavile(), new Bibarel()};
		Mon[][] teamList = {team1, team2, team3, team4, team5, team6, team7};
		return teamList;
	}
	/**
	 * Creates the challenges
	 */
	private void makeChallenges() {
        for (int i = 0; i < levelCount; i++) {
            Player enemy = playerList[i];
            String name = "Level " + (i + 1);

            challengesList[i] = new Challenges(name, enemy);
            //challengesList[i].unlock(); //Only Enable for testing
        }

        challengesList[0].unlock();
    }
	/**
	 * Creates the Player Objects with teams
	 * @param Mon[][] teamlist, a list composing of mon lists of teams
	 */
	private void makePlayers(Mon[][] teamList) 
	{
		for (int i = 0; i < levelCount; i++) 
		{
			playerList[i] = new Player(teamList[i]);
		}
	}
	/**
	 * Getter for challenge name
	 * @param Challenges challenge
	 * @return String name of challenge
	 */
	public String getName(Challenges challenge) 
	{
		return challenge.getName();
	}
	/**
	 * Getter for challenge name at index i
	 * @param int i for index
	 * @return String name of challenge at index i
	 */
	public String getName(int i) 
	{
		return challengesList[i].getName();
	}
	/**
	 * Getter for challenges in the Challenges list at i
	 * @param int i for index
	 * @return challenge at index i
	 */
	public Challenges getChallengesAt(int i)
	{
		return challengesList[i];
	}
	/**
	 * Gets Index of challenge in the challenge list
	 * @param Challenges challenge
	 * @return int index
	 */
	public int getIndexOf(Challenges challenge)
	{
		int i = 0;
		while (challenge != challengesList[i]) 
		{
			i++;
		}
		return i;
	}
	/**
	 * Unlocks the next level if there is one
	 * @param Challenges challenge, to mark as beat
	 * @return true if it unlocked a new level
	 */
	public boolean beatLevel(Challenges challenge) 
	{
		int i = getIndexOf(challenge);
		if (i < levelCount) 
		{
			if (challengesList[i+1].isLocked()) 
			{
				challengesList[i+1].unlock();
				return true;
			}
		}
		return false;
	}
	/**
	 * Getter for levelcount
	 * @return int levelCount
	 */
	public int levelCount() 
	{
		return levelCount;
	}
	
}
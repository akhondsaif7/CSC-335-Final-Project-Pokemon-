package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;

import javafx.application.Platform;
import javafx.concurrent.Task;
//import controller.NetworkController.ReadWriteServer;
import model.Mon;
import model.Move;
import model.Player;
import networking.PlayerID;


/**
 * The Controller following the MVC architecture that allows the view to cleanly interact with the various game elements.
 * Methods are implemented to allow the view to make a move for one of the {@link model.Player two players'} active {@link model.Mon Mon},
 * Retrieve either player's active Mon's movelist, switch the active Mon for a given player as well as allow a computer player to take an action.
 * @author Isa Abdulrazaq
 * @author Matt Lagier
 */
public class CombatController extends Observable implements Serializable {
	private static final long serialVersionUID = -6076316326175298527L;
	private volatile Player p1;
	private volatile Player p2;
	private boolean player1Turn = true;
	private boolean isMultiplayer = true;
	private boolean networkingMultiplayer = false;
	private int playerID;
	private boolean gameOver = false;
	private ReadWriteServer readWriteRun;
	
	private Thread readWriteThread;
	private boolean MPturn = true;
	
	private volatile Socket socket;
	private boolean actionTaken = false;

	
	/**
	 * Enum that controls which version of the computer AI is used by the {@link CombatController} class.
	 * Should be passed in as a parameter on construction of a {@link CombatController} object.
	 */
	public enum Difficulty {
		easy,
		medium,
		hard
	}
	
	private Difficulty diff;
	
	/**
	 * Constructor that takes references to the two player objects that will be involved in a given fight.
	 * The {@link model.Player Player} class holds all the relevant data for each player's team
	 * @param p1 In a singleplayer game, represents the player. In multiplayer, represents the server host.
	 * @param p2 In a singleplayer game, represents the computer player. In multiplayer, represents the server client.
	 * @param diff enum which describes which AI variant will be used by {@link #makeComputerMove()}
	 */
	public CombatController(Player p1, Player p2, Difficulty diff) {
		this.p1 = p1;
		this.p2 = p2;
		this.diff = diff;
	}
	
	/**
	 * @deprecated Please use {@link #CombatController(Player, Player, Difficulty)}.
	 * Constructor that takes references to the two player objects that will be involved in a given fight.
	 * The {@link model.Player Player} class holds all the relevant data for each player's team
	 * @param p1 In a singleplayer game, represents the player. In multiplayer, represents the server host.
	 * @param p2 In a singleplayer game, represents the computer player. In multiplayer, represents the server client.
	 */
	public CombatController(Player p1, Player p2) {
		this.p1 = p1;
		this.p2 = p2;
		diff = Difficulty.hard;
	}
	
	/**
	 * Convenience method to get all the moves associated the a player's active Mon. If an invalid player param is passed, an exception is thrown.
	 * In gameplay, this exception should never come up.
	 * @param player either 1 or 2 depending on whether the movelist for {@link #p1} or {@link #p2}'s active mon is desired.
	 * @return Move[] array containing the 4 (max) moves associated with a player's active mon.
	 */
	public Move[] getPlayerMoves(int player) {
		if (player == 1) {
			return p1.getActiveMon().getAllMoves();
		}
		else if (player == 2) {
			return p2.getActiveMon().getAllMoves();
		}
		else {
			throw new IllegalArgumentException("Invalid player index");
		}
	}
	
	/**
	 * Causes a player's active Mon to use the move given as a param and apply the relevant effect associated with it (typically damaging the other Mon).
	 * Notifies observers (the {@link view.PokemonView view} when a change is made to the {@link model.Player Player} objects and by extension their active Mons.
	 * @param player the player making the move (either 1 or 2)
	 * @param moveIndex the index in the Player's active Mon's movelist array corresponding to the selected move. (0-3)
	 * @throws InterruptedException 
	 */
	public void makeMove(int player, int moveIndex) {
		if (networkingMultiplayer) { updatePlayers(); }
		Mon p1Mon = p1.getActiveMon();
		Mon p2Mon = p2.getActiveMon();
		if (!networkingMultiplayer) {
			if (player == 1) {
				System.out.println("Player 1 used " + p1Mon.getMove(moveIndex).getName());
				Move currMove = p1Mon.getMove(moveIndex);
				currMove.applyEffect(p1Mon, p2Mon);
				actionTaken = true;
			}
			else if (player == 2) {
				System.out.println("Player 2 used " + p2Mon.getMove(moveIndex).getName());
				Move currMove = p2Mon.getMove(moveIndex);
				currMove.applyEffect(p2Mon, p1Mon);
				actionTaken = true;
			}
		} else if (networkingMultiplayer) {
			if (playerID == 1 && readWriteRun.getPlayerTurn()) {
				System.out.println("Player 1 used " + p1Mon.getMove(moveIndex).getName());
				Move currMove = p1Mon.getMove(moveIndex);
				currMove.applyEffect(p1Mon, p2Mon);
				actionTaken = true;
			} else if (playerID == 2 && readWriteRun.getPlayerTurn()) {
				System.out.println("Player 2 used " + p2Mon.getMove(moveIndex).getName());
				Move currMove = p2Mon.getMove(moveIndex);
				currMove.applyEffect(p2Mon, p1Mon);
				actionTaken = true;
			}
		}
		System.out.println(p1.getActiveMon().getCurrHp());
		ArrayList<Player> updateArray = new ArrayList<>();
		updateArray.add(p1); updateArray.add(p2);
		if (networkingMultiplayer && actionTaken) {
			updateThread();
			updatePlayers();
			swapPlayerTurn();
			try {
				notifyReadWrite();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			actionTaken = false;
		} else if (!networkingMultiplayer && actionTaken) {
			swapPlayerTurn();
			actionTaken = false;
		}
		
		setChanged();
		notifyObservers(updateArray);
		
	}
	
	/**
	 * Calls the player object's {@link model.Player#switchMon(int)} method on the appropriate player.
	 * Notifies observers (the {@link view.PokemonView view} when a change is made to the {@link model.Player Player} objects and by extension their active Mons.
	 * @param player the player whose active Mon is to be switched
	 * @param index the index of the Mon to switch to
	 */
	public void switchMon(int player, int index) {
		if (networkingMultiplayer) { updatePlayers(); }
		if (player == 1) {
			p1.switchMon(index);
		}
		else if (player == 2) {
			p2.switchMon(index);
		}
		ArrayList<Player> updateArray = new ArrayList<>();
		updateArray.add(p1); updateArray.add(p2);
		if (networkingMultiplayer) {
			updateThread();
			updatePlayers();
			try {
				notifyReadWrite();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setChanged();
		notifyObservers(updateArray);
	}
	
	/**
	 * Attempts to make a valid move for {@link #p2}. If no valid move is found (i.e p2 has lost), no move is made.
	 * If this method is called while p2's active Mon has fainted, it will switch to the next valid mon before making a move.
	 * Selects a move based on the {@link #diff} enum.
	 * easy = selects the weakest power move.
	 * medium = selects a random move.
	 * hard = selects the strongest power move.
	 * Previous implementation only supported hard AI.
	 */
	public void makeComputerMove() {
		if (p2.getActiveMon().hasFainted()) {
			if (p2.hasLost()) {
				return; // prevents move being made if p2 has lost
			}
			int nextLiving = 0;
			for (int i = 1; i < p2.teamSize(); i++) {
				if (!p2.getMon(i).hasFainted()) {
					nextLiving = i;
					break;
				}
			}
			switchMon(2, nextLiving);
		}
		int moveIndex;
		switch (diff) {
			case Difficulty.easy:
				moveIndex = getWorstMove();
				break;
			case Difficulty.medium: 
				moveIndex = getRandomMove();
				break;
			case Difficulty.hard:
				moveIndex = getBestMove();
				break;
			default:
				moveIndex = 0;
				break;
		}
		makeMove(2, moveIndex);
		swapPlayerTurn();
	}
	
	/**
	 * Finds the move with the highest power for {@link #p2}.
	 * @return moveIndex the index of the move with the highest power.
	 */
	private int getBestMove() {
		int moveIndex = 0;
		int maxPower = 0;
		Move[] moves = p2.getActiveMon().getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			if (moves[i].getPower() > maxPower && moves[i].getPP() > 0) {
				moveIndex = i;
				maxPower = moves[i].getPower();
			}
		}
		return moveIndex;
	}
	
	/**
	 * Finds the move with the lowest power for {@link #p2}.
	 * @return moveIndex the index of the move with the lowest power.
	 */
	private int getWorstMove() {
		int moveIndex = 0;
		int minPower = 0;
		Move[] moves = p2.getActiveMon().getAllMoves();
		for (int i = 0; i < moves.length; i++) {
			if (moves[i].getPower() < minPower && moves[i].getPP() > 0) {
				moveIndex = i;
				minPower = moves[i].getPower();
			}
		}
		return moveIndex;
	}
	
	/**
	 * Selects a random move for {@link #p2} to use.
	 * @return moveIndex the index of randomly selected move.
	 */
	private int getRandomMove() {
		Random rand = new Random();
		return rand.nextInt(0,4);
	}
	
	/**
	 * Returns a reference to the human player/server host
	 * @return {@link #p1}
	 */
	public Player getP1() {
		return p1;
	}
	
	/**
	 * Returns a reference to the computer player/client player
	 * @return {@link #p2}
	 */
	public Player getP2() {
		return p2;
	}
	
	/**
	 * Changes the reference to the human player/server host
	 * @param player a Player object that will hold the new human player/server host's team
	 */
	public void setP1(Player player) {
		p1 = player;
		updateThread();
	}
	
	/**
	 * Changes the reference to the computer player/client player
	 * @param player a Player object that will hold the new computer player/server client's team
	 */
	public void setP2(Player player) {
		p2 = player;
		updateThread();
	}
	
	/**
	 * Returns a boolean that identifies if the match is a multiplayer game or not, primarily used to identify a local match
	 * in combination with the networkingMultiplayer boolean.
	 * @return True if multiplayer, false if singleplayer
	 */
	public boolean getMultiplayer() {
		return isMultiplayer;
	}
	
	/**
	 * Sets the multiplayer boolean as required.
	 * 
	 * @param mp the boolean value that isMultiplayer should be set to.
	 */
	public void setMultiplayer(boolean mp) {
		isMultiplayer = mp;
	}
	
	/**
	 * Returns a boolean value if it's the players turn.
	 * @return True if players turn, false otherwise.
	 */
	public boolean getPlayerTurn() {
		return player1Turn;
	}
	
	/**
	 * Sets the current player turn boolean
	 * @param turn Sets player1Turn to the boolean provided.
	 */
	public void setPlayerTurn(boolean turn) {
		player1Turn = turn;
	}
	
	/**
	 * Swaps the player1Turn boolean, if its a networking game we update the thread with the boolean value
	 */
	public void swapPlayerTurn() {
		player1Turn = !player1Turn;
		if (networkingMultiplayer) {
			updateThread();
		}
		
	}
	
	/**
	 * Sets the networkingMultiplayer boolean to the provided boolean value.
	 * @param mp 
	 */
	public void setNetworkMultiplayer(boolean mp) {
		networkingMultiplayer = mp;
	}
	
	/**
	 * Update call the notify observers function when the thread needs to update the view and has passed that information to the controller.
	 */
	public void updateView() {
		ArrayList<Player> updateArray = new ArrayList<>();
		updateArray.add(p1); updateArray.add(p2);
		setChanged();
		notifyObservers(updateArray);
	}
	
	/**
	 * Set the players ID, used to determine which player is which in networking games and to determine which branch of code the
	 * thread should run.
	 * @param id integer value of the order in which players connect to the server
	 */
	public void setPlayerID(int id) {
		this.playerID = id;
	}
	
	/**
	 * Returns the integer value of the player ID
	 * @return
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/**
	 * Switch the gameOver boolean when the match is ended. Used to stop the update() function in the view from displaying two
	 * game over screens since the thread calls the update an extra time function too quick.
	 */
	public void switchGameOver() {
		gameOver = !gameOver;
	}
	
	/**
	 * Returns the value of the gameOver boolean
	 * @return True means the game is over, false means its still in progress.
	 */
	public boolean getGameOver() {
		return gameOver;
	}
	
	/**
	 * Returns the boolean value networkingMultiplayer which is used to determine if the game is a networking based multiplayer match.
	 * Used in conjunction with isNetworking to determine if the game is local or networked as well as separate it from the singleplayer.
	 * @return True if networking, false otherwise.
	 */
	public boolean getNetworkMultiplayer() {
		return networkingMultiplayer;
	}
	
	/**
	 * Update the controller with the thread object running the networking for the client.
	 * @param rwr The object currently running the thread.
	 */
	public void updateRWR(ReadWriteServer rwr) {
		this.readWriteRun = rwr;
	}
	
	/**
	 * Returns the object currently running the thread.
	 * @return ReadWriteServer object running the networking thread.
	 */
	public ReadWriteServer getRWR() {
		return readWriteRun;
	}
	
	/**
	 * Updates the controller players with the players stored in the networking thread.
	 */
	public void updatePlayers() {
		this.p1 = readWriteRun.getp1();
		this.p2 = readWriteRun.getp2();
		updateView();
	}
	
	/**
	 * Updates the thread with the current players stored in the controller.
	 */
	public void updateThread() {
		if (networkingMultiplayer) {
			readWriteRun.setP1(p1);
			readWriteRun.setP2(p2);
			readWriteRun.setPlayerTurn(player1Turn);
			readWriteRun.changeTurn(player1Turn);
			readWriteRun.setPlayerID(playerID);
		}
	}
	
	/**
	 * Forces a thread to wait, used to ensure the server is "turn" based
	 * @throws InterruptedException
	 */
	public void waitReadWrite() throws InterruptedException {
		synchronized (readWriteRun) {
			readWriteRun.wait();
		}
		
	}
	
	/**
	 * Releases the thread from its wait status to continue processing commands.
	 * @throws InterruptedException
	 */
	public void notifyReadWrite() throws InterruptedException {
		synchronized (readWriteRun) {
			readWriteRun.notify();
		}
	}
	
	/**
	 * Adds the thread object to the controller.
	 * @param rwt
	 */
	public void setReadWriteThread(Thread rwt) {
		this.readWriteThread = rwt;
	}
	
	/**
	 * Returns the turn that is used by the thread to determine whose turn it is. Used for networking matches.
	 * @return True if player turn, false otherwise.
	 */
	public boolean getMPTurn() {
		return MPturn;
	}
	
	/**
	 * Sets the MPTurn boolean based on the provided argument
	 * @param turn
	 */
	public void setMPTurn(boolean turn) {
		MPturn = turn;
	}
	
	/**
	 * connectToServer facilitates the clients connection to the server. It takes an ipAddress as input and tries to connect.
	 * It then gets its assigned ID, then creates a thread that runs the networking code in the background.
	 * @param ipAddress
	 */
	public void connectToServer(String ipAddress) {
		try {
			this.socket = new Socket(ipAddress, 4000);
			System.out.println(socket.isConnected() + " Is connected");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Object idObj = in.readObject();
			PlayerID id = (PlayerID) idObj;
			playerID = id.getID();
			this.setPlayerID(playerID);
			System.out.println("Player number: " + playerID);
			if (playerID == 1) {
				System.out.println("Waiting for Player 2 to connect...");
			}
			readWriteRun = new ReadWriteServer(in, out);
			updateRWR(readWriteRun);
			updateThread();
			
			readWriteThread = new Thread(readWriteRun);
			setReadWriteThread(readWriteThread);
			readWriteThread.start();
			
			
		} catch(IOException e) {
			System.out.println("Controller IO exception when connecting to server: Check ip address. " + e);
		} catch(ClassNotFoundException e) {
			System.out.println("Class broken or something");
		}
	}
	
	/**
	 * ReadWriteServer handles the clients networking logic. It is created by connectToServer and runs until the game ends. The
	 * combat controller is updated based on the information received from the server and the view is updated when new objects are
	 * being read from the server.
	 */
	public class ReadWriteServer implements Runnable {
		private ObjectInputStream dataIn;
		private ObjectOutputStream dataOut;
		private PlayerTurnController turn = new PlayerTurnController(true);
		
		private volatile Player player1;
		private volatile Player player2;
		
		private boolean playerTurn;
		private int playerID;
		
		int index = 0;
		
		private boolean justUpdatedP1 = false;
		private boolean justUpdatedP2 = true;
		
		String testCheck = "";
		String testCheck2 = "";
		
		public ReadWriteServer(ObjectInputStream in, ObjectOutputStream out) {
			dataIn = in;
			dataOut = out;
			turn = new PlayerTurnController(true);
		}

		/**
		 * Sets the objects player 1 to a provided Player object. Used to update the thread using the controllers Player object.
		 * @param p1 Player object to replace p1 in the thread.
		 */
		public void setP1(Player p1) {
			this.player1 = p1;
		}
		
		/**
		 * Sets the objects player 2 to a provided Player object. Used to update the thread using the controllers Player object.
		 * @param p2 Player object to replace p2 in the thread.
		 */
		public void setP2(Player p2) {
			this.player2 = p2;
		}
		
		/**
		 * Returns the player 1 value stored in the thread.
		 * @return Player object stored in player 1
		 */
		public Player getp1() {
			return player1;
		}
		
		/**
		 * Returns the player 2 object stored in the thread.
		 * @return Player objected stored in player 2
		 */
		public Player getp2() {
			return player2;
		}
		
		/**
		 * Set the player turn, used to update the thread using the combat controllers value.
		 * @param turn boolean value the turn should be.
		 */
		public void setPlayerTurn(boolean turn) {
			this.playerTurn = turn;
		}
		
		/**
		 * Returns the players turn boolean.
		 * @return
		 */
		public boolean getPlayerTurn() {
			return playerTurn;
		}
		
		/**
		 * Sets the player ID, used during the connection process.
		 * @param playerID integer value representing the player ID.
		 */
		public void setPlayerID(int playerID) {
			this.playerID = playerID;
		}
		
		/**
		 * Change the current turn, used to update the turn from the controllers value.
		 * @param changeTurn True/False representing the players turn.
		 */
		public void changeTurn(boolean changeTurn) {
			this.turn.setPlayerTurn(changeTurn);
		}
		
		
		/**
		 * run contains the logic the thread will operate on. First the thread sends the current players object to the server based on their
		 * player ID. Then, depending on the ID, it reads, saves and updates the view or sends the current state of the game to the server.
		 */
		@Override
		public void run() {
			try {
				waitReadWrite();
			} catch (InterruptedException e) {
				System.out.println("Wait exception in controller: " + e);
			}
			try {
				switch(playerID) {
					case 1:
						updateThread();
						player1 = getP1();
						dataOut.writeObject(player1);
						player2 = (Player) dataIn.readObject();
						updatePlayers();
						break;
					case 2:
						updateThread();
						player2 = getP1();
						dataOut.writeObject(player2);
						player1 = (Player) dataIn.readObject();
						setMPTurn(false);
						updatePlayers();
						break;
				}
				
				while(true) {
					// Switch cases correspond to the players join order to the server. PlayerID 1 will perform only case 1 actions
					// while player ID 2 only performs its actions. Its to prevent the threads from getting lost in code they shouldn't
					// be executing.
					switch(playerID){
						case 1:
							// Just updated is a flag set by the opposite thread
							if (justUpdatedP1) {
								Player temp1 = (Player) dataIn.readObject();
								Player temp2 = (Player) dataIn.readObject();
								if (temp1 != null) {
									player1 = null;
									player1 = temp1;
								}
								if (temp2 != null) {
									player2 = null;
									player2 = temp2;
								}
								setMPTurn(true);
								Platform.runLater( () -> {
									updatePlayers();
								});
								//updatePlayers();
								//updateView();
								justUpdatedP1 = false;
								playerTurn = true;
							}
							
							try {
								waitReadWrite();
							} catch (InterruptedException e) {
								System.out.println("Wait exception in controller: " + e);
							}
							if (!justUpdatedP1) {
								updateThread();
								dataOut.writeObject(player1);
								dataOut.writeObject(player2);
								setMPTurn(false);
								justUpdatedP1 = true;
								playerTurn = false;
								Platform.runLater( () -> {
									updateView();
								});
							}
							break;
						case 2:
							if (justUpdatedP2) {
								Player temp1 = (Player) dataIn.readObject();
								Player temp2 = (Player) dataIn.readObject();
								if (temp1 != null) {
									player1 = null;
									player1 = temp1;
								}
								if (temp2 != null) {
									player2 = null;
									player2 = temp2;
								}
								setMPTurn(true);
								setMPTurn(true);
								Platform.runLater( () -> {
									updatePlayers();
								});
								//updatePlayers();
								//updateView();
								justUpdatedP2 = false;
								playerTurn = true;
							}
							try {
								waitReadWrite();
							} catch (InterruptedException e) {
								System.out.println("Wait exception in controller: " + e);
							}
							if (!justUpdatedP2) {
								updateThread();
								dataOut.writeObject(player1);
								dataOut.writeObject(player2);
								setMPTurn(false);
								justUpdatedP2 = true;
								playerTurn = false;
								Platform.runLater( () -> {
									updateView();
								});
							}
						break;							
					}
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						System.out.println("Sleep error: " + e);
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("WTS run: " + e);
			}
		}
	}
	
}
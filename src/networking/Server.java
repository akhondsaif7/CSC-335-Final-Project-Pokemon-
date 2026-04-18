package networking;

import java.io.*;
import java.net.*;

import controller.CombatController;
import controller.PlayerTurnController;
import model.Mon;
import model.Player;
import mons.Bibarel;

/**
 * Handles the local networking between game clients. Stores data for the two
 * threads ensuring the most up to date information is being sent. It also
 * handles the creation of {@link ReadWrite} objects that act as the two thread
 * to move data between the server and its two maximum allowed clients.
 * 
 * The server (S) communications follow a strict path for Player 1 (P1) and
 * Player 2 (P2). P1 -> S -> P2 -> S -> P1. This path repeats until the game is
 * complete or the server shuts down.
 * 
 * @author Matt Lagier
 */
public class Server implements Runnable {

	private ServerSocket servSocket;
	private int numPlayers = 0;
	private int maxPlayers = 2;
	private int playerID;

	private Thread readThread1;
	private Thread readThread2;
	private volatile boolean thread1Wait = false;
	private volatile boolean thread2Wait = false;

	private Socket p1Sock;
	private Socket p2Sock;
	private ReadWrite readWriteP1;
	private ReadWrite readWriteP2;

	private volatile Player p1;
	private volatile Player p2;
	
	private static Thread thread;

	public Server() {
		System.out.println("Sever Started");
		try {
			servSocket = new ServerSocket(4000);
		} catch (IOException e) {
			System.out.println("Game Sever IO Exception");
		}
	}

	public Player getPlayer1() {
		return p1;
	}

	public Player getPlayer2() {
		return p2;
	}

	private boolean getThread1Status() {
		return thread1Wait;
	}

	private boolean getThread2Status() {
		return thread2Wait;
	}

	private void setThread1Status(boolean status) {
		thread1Wait = status;
	}

	private void setThread2Status(boolean status) {
		thread2Wait = status;
	}

	/**
	 * Returns a boolean on the status of both thread#Wait booleans, used for the
	 * {@link #releaseBothThreads()} method.
	 * 
	 * @return returns a boolean if both threads are currently waiting.
	 */
	private boolean checkBothThreadsWait() {
		return (getThread1Status() && getThread2Status());
	}

	/**
	 * At the start of a game, each players client sends a copy of their play object
	 * to the server. In order to make sure the server sends back a copy of a
	 * non-null object, I needed a way to pause the threads until both of them we're
	 * ready. This forces each thread to get stuck into a loop that does nothing
	 * until both threads switch the thread#Wait boolean. This prevents the server
	 * from sending a null type to a player if there was an asynchronous load time
	 * between the two clients. For instance if player 1 joined 10 seconds before
	 * player 2, player 1 may receive a null type when the thread pulls the data
	 * from the server, instead of a player object. My other attempts at looping in
	 * the thread didn't work out and this was the best option I could come up with.
	 * 
	 * @throws InterruptedException if there is an error when sleeping
	 */
	private void releaseBothThreads() throws InterruptedException {
		while (!checkBothThreadsWait()) {
			// Silly version of pausing both threads but I couldn't figure that a good way
			// so we're going to loop and do nothing until both players are ready and have
			// been updated on the server.
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Sleep error in server: " + e);
		}
	}

	/**
	 * Force server thread 1 to wait, used for timing of server updates between the
	 * players.
	 * 
	 * @throws InterruptedException if there is an interruption.
	 */
	public void makeReadWriteP1Wait() throws InterruptedException {
		synchronized (readWriteP1) {
			readWriteP1.wait();
		}

	}

	/**
	 * Force server thread 2 to wait, used for timing of server updates between the
	 * players.
	 * 
	 * @throws InterruptedException if there is an interruption.
	 */
	public void makeReadWriteP2Wait() throws InterruptedException {
		synchronized (readWriteP2) {
			readWriteP2.wait();
		}

	}

	/**
	 * Notifies server thread 1 to begin processing commands again.
	 * 
	 * @throws InterruptedException if there is an interruption.
	 */
	public void makeReadWriteP1Notify() throws InterruptedException {
		synchronized (readWriteP1) {
			readWriteP1.notify();
		}
	}

	/**
	 * Notifies server thread 2 to begin processing commands again.
	 * 
	 * @throws InterruptedException if there is an interruption.
	 */
	public void makeReadWriteP2Notify() throws InterruptedException {
		synchronized (readWriteP2) {
			readWriteP2.notify();
		}
	}

	public void setPlayer1Arg(Player one) {
		this.p1 = one;
	}

	public void setPlayer2Arg(Player two) {
		this.p2 = two;
	}

	/**
	 * Update the server with thread 1s copy of the two player objects. This allows
	 * threads to pull the most current data from the server since the threads
	 * cannot talk to each other.
	 */
	public void updateServerP1Thread() {
		System.out.println("----------P1 ADDED TO SERVER----------");
		this.p1 = readWriteP1.getPlayer1Thread();
		this.p2 = readWriteP1.getPlayer2Thread();
	}

	/**
	 * Update the server with thread 2s copy of the two player objects. This allows
	 * threads to pull the most current data from the server since the threads
	 * cannot talk to each other.
	 */
	public void updateServerP2Thread() {
		System.out.println("--------------------P2 ADDED TO SERVER--------------------");
		this.p1 = readWriteP2.getPlayer1Thread();
		this.p2 = readWriteP2.getPlayer2Thread();
	}

	/*
	 * acceptConnection facilitates the creation of sockets/connection between both
	 * players and the server. It also assigns each player an ID which is used by
	 * the below threads to determine which commands to execute based on their join
	 * order and the flow of the game. ReadWrite objects are created and control the
	 * flow of information between the server and the clients.
	 */
	public void acceptConnection() throws ClassNotFoundException, InterruptedException {
		try {
			System.out.println("Waiting for players");
			try {
				while (numPlayers < maxPlayers) {
					Socket sock = servSocket.accept();
					ObjectOutputStream outgoingData = new ObjectOutputStream(sock.getOutputStream());
					outgoingData.flush();
					ObjectInputStream incomingData = new ObjectInputStream(sock.getInputStream());
					numPlayers += 1;
	
					PlayerID pID = new PlayerID(numPlayers);
					outgoingData.writeObject(pID);
					playerID = pID.getID();
	
					System.out.println("Connected as Player " + numPlayers);
	
					ReadWrite rw = new ReadWrite(numPlayers, incomingData, outgoingData);
	
					if (numPlayers == 1) {
						p1Sock = sock;
						readWriteP1 = rw;
					} else {
						p2Sock = sock;
						readWriteP2 = rw;
					}
				}
			
	
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					System.out.println("Sleep error in server: " + e);
				} 
				readThread1 = new Thread(readWriteP1);
				readThread2 = new Thread(readWriteP2);
	
				readThread1.start();
				readThread2.start();
				} catch (IOException e) {
					System.out.println("IO Exception in server: " + e);
				} finally {
					while ((!(p1Sock == null)) && (!(p2Sock == null))) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							System.out.println("Sleep error in server: " + e);
						} 
					}
				}	
		} catch (Exception e) {
			System.out.println("Accept Connection IO Exception: " + e);
		}
	}

	/**
	 * The ReadWrite class utilizes the socket connections made in
	 * {@link Server#acceptConnection()} to push data between the server and client.
	 * It implements the Runnable interface to allow for multi-threading
	 * capabilities.
	 * 
	 * @author Matt Lagier
	 */
	private class ReadWrite implements Runnable {
		private int playerID;
		private ObjectInputStream dataIn;
		private ObjectOutputStream dataOut;

		private volatile Player p1Thread;
		private volatile Player p2Thread;

		public ReadWrite(int id, ObjectInputStream in, ObjectOutputStream out) {
			playerID = id;
			dataIn = in;
			dataOut = out;
		}

		/**
		 * Updates the threads player objects to the ones stored in the server.
		 */
		private void updatePlayer() {
			this.p1Thread = getPlayer1();
			this.p2Thread = getPlayer2();
		}

		private Player getPlayer1Thread() {
			return this.p1Thread;
		}

		private Player getPlayer2Thread() {
			return this.p2Thread;
		}

		/**
		 * Contains all the logic in the operation of the thread split into two parts
		 * that handle either player 1 or player 2s specific requirements in the
		 * architecture of the program. At the start of the match, the thread reads each
		 * players specific object (player 1 looks for player 1), and writes it to the
		 * server. Once both objects are written to the server, it sends the opposite
		 * object out to the player (player 1 gets player2). This allows for the clients
		 * to have the other players team loaded correctly. From there the flow of the
		 * networking is (-> indicates sending information) P1 -> Server -> P2 -> Server
		 * -> P1 (repeat). Each player sends a copy of both player objects from their
		 * view, and the server then stores and sends those objects to the other player.
		 * This is accomplished by having the threads wait at specific points for the
		 * other thread to do its work, then releases the thread while waiting itself.
		 */
		@Override
		public void run() {
			try {
				System.out.println(
						"P1 Sock Status: " + p1Sock.isConnected() + " - P2 Sock Status: " + p2Sock.isConnected());
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					System.out.println("Sleep error in server: " + e);
				}
				// Update the opponent at the start of the game with the players current object.
				// Ensures both players teams are synced.
				switch (playerID) {
				case 1:
					// Read in the player object once the player has marked themselves as ready, save it to the thread
					Player temp1 = (Player) dataIn.readObject();
					p1Thread = temp1;
					// Save the p1 object to the server
					setPlayer1Arg(p1Thread);
					// Mark the thread as ready to move on
					setThread1Status(true);
					// Enter that loop until P2 thread is ready
					releaseBothThreads();
					// Get the player objects from the server
					updatePlayer();
					// Send P2s object to player 1
					dataOut.writeObject(p2Thread);
					break;
				case 2:
					Player temp2 = (Player) dataIn.readObject();
					p2Thread = temp2;
					setPlayer2Arg(p2Thread);
					setThread2Status(true);
					releaseBothThreads();
					updatePlayer();
					dataOut.writeObject(p1Thread);
					break;
				}

				// Main connection logic that runs until the end of the game.
				while (!p1Sock.isClosed() && !p2Sock.isClosed()) {
					System.out.println("Status of p1Sock and p2Sock: " + (p1Sock == null) + " - " + (p2Sock == null));
					switch (playerID) {
					// Player 1 specific code
					case 1:
						// Read in the player objects
						Player temp1 = (Player) dataIn.readObject();
						Player temp2 = (Player) dataIn.readObject();
						// If something went wrong and the objects are null, skip the saving process
						if (temp1 != null) {
							// Save non-null Player objects to the threads variable
							p1Thread = temp1;
							if (getPlayer1() != null) {
								System.out.println("Server player 1: " + getPlayer1().getActiveMon().getName());
							}

							System.out.println("cont 1, player 1" + p1Thread.getActiveMon().getName());
						}
						if (temp2 != null) {
							p2Thread = temp2;
							if (getPlayer2() != null) {
								System.out.println("Server player 1: " + getPlayer2().getActiveMon().getName());
							}
							System.out.println("Cont 1, player 2: " + p2Thread.getActiveMon().getName());
						}
						// Update the server with the P1 Threads player objects
						updateServerP1Thread();
						// Notify P2 thread to begin working
						makeReadWriteP2Notify();
						// Wait until P2 thread gets done with its tasks
						makeReadWriteP1Wait();
						// Now that P2 thread is done, update this thread with the new Player objects from P2 
						updatePlayer();
						// Send the updated player objects to the P1 client, repeat.
						dataOut.writeObject(p1Thread);
						dataOut.writeObject(p2Thread);
						break;
					// Player 2 specific code
					case 2:
						makeReadWriteP2Wait();
						updatePlayer();
						dataOut.writeObject(p1Thread);
						dataOut.writeObject(p2Thread);
						Player tempP1 = (Player) dataIn.readObject();
						Player tempP2 = (Player) dataIn.readObject();
						if (tempP1 != null) {
							p1Thread = tempP1;
							if (getPlayer1() != null) {
								System.out.println("Server player 1: " + getPlayer1().getActiveMon().getName());
							}
						}
						System.out.println("Cont 2, player 1" + p1Thread.getActiveMon().getName());
						if (tempP2 != null) {
							p2Thread = tempP2;
							if (getPlayer2() != null) {
								System.out.println("Server player 1: " + getPlayer2().getActiveMon().getName());
							}
						}
						System.out.println("Cont 2, player 2" + p2Thread.getActiveMon().getName());
						updateServerP2Thread();
						makeReadWriteP1Notify();
					}

					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						System.out.println("Sleep error in server: " + e);
					}
				}

			} catch (IOException | ClassNotFoundException | InterruptedException e) {
				System.out.println("Error in read write: " + e);
			}
		}
	}

	/**
	 * Static method to start the server when the "Host server" button is clicked in
	 * the multiplayer menu. The server can also be launched separately from eclipse
	 * if you want to see the print commands.
	 * 
	 * @param args not used.
	 */
	public static void main(String[] args) {
		Server serv = new Server();
		thread = new Thread(serv);
		thread.start();
	}

	/**
	 * Server starts the actual connection process in a way that allows for
	 * multithreading.
	 */
	@Override
	public void run() {
		try {
			acceptConnection();
		} catch (ClassNotFoundException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
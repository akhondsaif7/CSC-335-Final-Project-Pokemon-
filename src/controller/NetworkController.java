package controller;

/*
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import model.Player;
import networking.PlayerID;
import view.PokemonView;

public class NetworkController {
	CombatController cc;
	PokemonView view;
	ReadWriteServer readWriteRun;
	//private ReadFromServer rfsRun;
	//private WriteToServer wtsRun;
	
	private Socket socket;
	private int playerID;
	
	
	public NetworkController(CombatController cc) {
		this.cc = cc;
	}
	
	public void updateController(CombatController cc) {
		this.cc = cc;
	}

	public void connectToServer(String ipAddress) {
		cc.setNetworkMultiplayer(true);;
		try {
			socket = new Socket(ipAddress, 4000);
			System.out.println(socket.isConnected() + " Is connected");
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Object idObj = in.readObject();
			PlayerID id = (PlayerID) idObj;
			playerID = id.getID();
			cc.setPlayerID(playerID);
			System.out.println("Player number: " + playerID);
			if (playerID == 1) {
				System.out.println("Waiting for Player 2 to connect...");
			}
			readWriteRun = new ReadWriteServer(in, out);
			Thread readWriteThread = new Thread(readWriteRun);
			readWriteThread.start();
			//rfsRun = new ReadFromServer(in);
			//wtsRun = new WriteToServer(out);
			//Thread readThread = new Thread(rfsRun);
			//Thread writeThread = new Thread(wtsRun);
			//readThread.start();
			//writeThread.start();
		} catch(IOException e) {
			System.out.println("Controller IO exception when connecting to server: Check ip address. " + e);
		} catch(ClassNotFoundException e) {
			System.out.println("Class broken or something");
		}
	}
	
	private class ReadWriteServer implements Runnable {
		private ObjectInputStream dataIn;
		private ObjectOutputStream dataOut;
		
		public ReadWriteServer(ObjectInputStream in, ObjectOutputStream out) {
			dataIn = in;
			dataOut = out;
		}

		@Override
		public void run() {
			System.out.println("Player ID: " + playerID);
			System.out.println("Player 1 turn: " + cc.getPlayerTurn());
			try {
				while(true) {
					if (cc.getPlayerTurn() && cc.getPlayerID() == 1) {
						dataOut.writeObject(cc.getP1());
						dataOut.writeObject(cc.getP2());
						dataOut.flush();

					} 
					else if (!cc.getPlayerTurn() && cc.getPlayerID() == 1) {
						Player temp1 = (Player) dataIn.readObject();
						Player temp2 = (Player) dataIn.readObject();
						System.out.println("Read object: " + temp1.toString());
						cc.setP2(temp1);
						cc.setP2(temp2);
					}
					
					else if (cc.getPlayerTurn() && cc.getPlayerID() == 2) {
						Player temp1 = (Player) dataIn.readObject();
						Player temp2 = (Player) dataIn.readObject();
						cc.setP2(temp1);
						cc.setP2(temp2);
					}
					
					else if (!cc.getPlayerTurn() && cc.getPlayerID() == 2){
						dataOut.writeObject(cc.getP2());
						dataOut.writeObject(cc.getP1());
						dataOut.flush();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("Sleep error: " + e);
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("WTS run: " + e);
			}
		}
	}
} */
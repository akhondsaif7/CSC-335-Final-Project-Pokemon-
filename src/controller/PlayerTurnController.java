package controller;

import java.io.Serializable;

public class PlayerTurnController implements Serializable{
	private static final long serialVersionUID = -4453692356762964596L;
	private boolean isPlayerTurn;
	
	public PlayerTurnController(boolean turn) {
		this.isPlayerTurn = turn;
	}
	
	public boolean getPlayerTurn() {
		return isPlayerTurn;
	}
	
	public void switchPlayerTurn() {
		isPlayerTurn = !isPlayerTurn;
	}
	
	public void setPlayerTurn(boolean turn) {
		isPlayerTurn = turn;
	}

}

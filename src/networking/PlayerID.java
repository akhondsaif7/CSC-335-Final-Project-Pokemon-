package networking;
import java.io.Serializable;

public class PlayerID implements Serializable {
		private static final long serialVersionUID = 7357357517035316645L;
		private int playerID;
		
		public PlayerID(int id) {
			playerID = id;
		}
		
		public int getID() {
			return playerID;
		}
	}
package game;

import javafx.scene.paint.Color;

/**
 * @author Ee En Goh - 17202691
 *
 */
public class PlayerController {

	// Variables

	/** Player that is using black checker */
	private  static Player playerB;		// player 1

	/** Player that is using white checker */
	private  static Player playerW;		// player 2

	/** Player in this game round */
	private  static Player currentPlayer;

	/** The opponent player */
	private  static Player opponentPlayer;




	// End of variables

	// Set up methods

	PlayerController() {
		playerW = new Player(null, Color.WHITE);
		playerB = new Player(null, Color.BLACK);
		currentPlayer = playerW;
		opponentPlayer = playerB;
	}








	// End of set up methods

	// GETTERS AND SETTERS

	/**
	 * @return	The name of current player
	 */
	public String getCurrentPlayerName() {
		return currentPlayer.getPlayerName();
	}

	public void setCurrentPlayerName(String name) {
		currentPlayer.setPlayerName(name);
	}

	/**
	 * @return	The disk color of the current player using
	 */
	public String getCurrentPlayerColor() {
		if(currentPlayer.getColor() == Color.BLACK)
			return "black";
		return "white";
	}

	/**
	 * @param color The new disk color given to the current player
	 */
	public void setCurrentPlayerColor(String color) {
		if(color.compareTo("black") == 0)
			currentPlayer.setColor(Color.BLACK);
		else
			currentPlayer.setColor(Color.WHITE);
	}



	// END OF GETTERS AND SETTERS

	// methods
	// ----- Functionalities -----

	/**
	 * Switch the current player when the game turn progress
	 */
	public void changeCurrentPlayer() {
		Player temp = opponentPlayer;
		opponentPlayer = currentPlayer;
		currentPlayer = temp;
	}

	/**
	 * @return The information about the current player
	 */
	public String displayCurrentPlayerInfo() {
		return getCurrentPlayerName() + ", your disk color is " + getCurrentPlayerColor().toUpperCase() + "\n";
	}

	/**
	 * Method that shows if the current player is using the disk with target color
	 * @param s	String, which indicates the target disk color
	 * @return True, if the current player is using the disk color same with the target
	 */
	public boolean isColorEqual(String color) {
		if(getCurrentPlayerColor().compareTo(color) == 0)
			return true;
		return false;
	}

	/**
	 * Boolean method to check if the current player has disk in jail
	 * @return True if there is, else no
	 */
	public boolean currentPlayerDiskInJail() {
		return currentPlayer.getPlayerJailState();
	}
}

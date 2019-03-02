package game;

import javafx.scene.paint.Color;

/**
 * @author YeohB
 *
 */
public class PlayerController {

	/** Player that is using black checker */
	private Player playerB;
	
	/** Player that is using white checker */
	private Player playerW;
	
	/** Player in this game round */
	private Player currentPlayer;
	
	/** The opponent player */
	private Player opponentPlayer;
	
	PlayerController() {
		playerW = new Player(null, Color.WHITE);
		playerB = new Player(null, Color.BLACK);
		currentPlayer = playerW;
		opponentPlayer = playerB;
	}
	
	/**
	 * Switch the current player when the game turn progress
	 */
	public void changeCurrentPlayer() {
		Player temp = opponentPlayer;
		opponentPlayer = currentPlayer;
		currentPlayer = temp;
	}
	
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
/*
	public boolean isValidColor(String color) {
		return ("black".compareTo(color) == 0) || ("white".compareTo(color) == 0);
	}
*/
}

package game;

import javafx.scene.paint.Color;

/**
 * The class that has the information about a player, including the name, the current play score and the disk color used 
 * @author YeohB - 17357376
 * @author Ee En Goh - 17202691
 */

public class Player {

	/* Instance variables */
	
	/** The unique player's name */
	private String playerName;
	
	/** Total score earned by this player */
	private int score;
	
	/** The color of disk that this player is using */
	private Color color;
	
	/** The degree of rotation of the game board for better user experience */
	// private int rotation;
	
	/**
	 * Constructs a player object - essentially creating a player for the game
	 * @param name
	 * @param color
	 */
	Player(String name, Color color) {
		this.playerName = name;
		this.color = color;
		score = 0;
	}
	
	/**
	 * Set the current player's name
	 * @param name	The new name given to this player
	 */
	public void setPlayerName(String name) {
		this.playerName = name;
	}
	
	/**
	 * Increase the score that brings a disk home
	 */
	public void setScore() {
		this.score++;
	}
	
	/**
	 * @return The current player's name
	 */
	public String getPlayerName() {
		return this.playerName;
	}
	
	/**
	 * @return The total score obtained by the current player’s
	 */
	public int getScore() {
		return this.score;
	}
	
	/**
	 * @return Color class, the disk color used by the current player
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Set the color of disks that used by the current player
	 * @param color	Color object, the color of disk given, either BLACK or WHITE
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}

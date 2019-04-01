package game;

/**
 * A class that hold the responses to any defined event, like message output or instructions on the output field box. 
 * 
 * @author Ee En Goh - 17202691
 * @author Ferdia Fagan - 16372803
 */
public class EventsController {
	
	/** int variable to keep tracking the number of game turn since starts */
	private int turnCount;
	
	/** Boolean variable to indicate if it is the end of current game round */
	private boolean endOfTurn;
	
	/** Numbers of player that has done the player name initialization */
	private int initializedPlayer;
	
	EventsController(){
		turnCount = 0;		// TODO
		initializedPlayer = 0;
		endOfTurn = false;
	}
	
	/**
	 * @return The respond message to notify the player that it is his/her game turn
	 */
	public String promptPlayerWithName(String currentPlayerColor) { // TODO
		return currentPlayerColor + " its your turn!";
	}
	
	/** @return The response message to notify the player n to initialize his/her player name*/
	public String promptPlayerForName() {
		return "Player " + ( getInitializedPlayer() + 1 ) + ", please enter your name using [.name] [stringName]: ";
	}
	
	/** @return String message to notify the player to roll dice (ie. click the dice-roll button) */
	public String promptPlayerToRollDice() {
		return ", please roll the dice (Red Button)";
	}
	
	/** @return String message to inform the player to make a valid dice move by command */
	public String promptPlayerToMove() {
		return "Select a move from the below list with: [.move] [moveNumber]";
	}
	
	/** @return String message to inform the player to go to the next game turn  */
	public String promptPlayerToEnterNext() {
		return "Press next to end turn";
	}
	
	/** @return String message to announce the starting player, based on the comparing of starting dice value */
	public String announceStartingPlayer() {
		return " , you rolled the highest dice value, you will go first";
	}
	
	/** @return Get the number of current game turn */
	public int getTurnCount() {
		return this.turnCount;
	}
	
	public int getInitializedPlayer(){
		return this.initializedPlayer;
	}
	
	/** Increase the current game turn, by following the game progress */
	public void setTurnCount() {
		++ this.turnCount;
		// TODO DEBUG
		// System.out.println(" Current turn count : " + turnCount);
	}
	
	public void setInitializedPlayer(){
		++ this.initializedPlayer;
	}
	
	public void setEndOfTurn(boolean end) {
		endOfTurn = end;
	}
	
	public boolean isEndOfTurn() {
		return this.endOfTurn;
	}
}

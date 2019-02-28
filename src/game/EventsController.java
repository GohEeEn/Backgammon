package game;

/**
 * A class that hold the responses to any defined event, like message output or instructions on the output field box. 
 * @author YeohB - 17357376
 * @author Ee En Goh - 17202691
 */
public class EventsController {
	
	/** int variable to keep tracking the number of game turn since starts */
	private int turnCount;
	
	/** Boolean variable to indicate if it is the end of current game round */
	private boolean endOfTurn;
	
	EventsController(){
		turnCount = 0;		// TODO
		endOfTurn = false;
	}
	
	/** @return The response message to notify the player n to initialize his/her player name*/
	public String promptPlayerForName() {
		return "Player " + ( getTurnCount( ) + 1 ) + " , please enter your name using [.name] [stringName]: ";
	}
	
	/** @return String message to notify the player to roll dice (ie. click the dice-roll button) */
	public String promptPlayerToRollDice() {
		return ", please roll the dice (Red Button)";
	}
	
	/** @return String message to inform the player to make a valid dice move by command */
	public String promptPlayerToMove() {
		return "Select a move - [.move] [ moveFrom | jail ] [ moveTo | jail ]";
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
	
	/** Increase the current game turn, by following the game progress */
	public void setTurnCount() {
		this.turnCount++;
		// TODO DEBUG
		// System.out.println(" Current turn count : " + turnCount);
	}
	
	public void setEndOfTurn(boolean end) {
		endOfTurn = end;
	}
	
	public boolean isEndOfTurn() {
		return this.endOfTurn;
	}

	public void startGame() {

	}

}

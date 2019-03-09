package game;

import java.util.ArrayList;

import data_structures.StackEmptyException;
import gui.Board;
import gui.EndGame;
import gui.TextBox;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Class that consists of operations that control the flow of the backgammon game
 * 
 * @author Ee En Goh - 17202691
 * @version This class will control the logistics of the game, and not the mechanics. To be finished
 */
public class GameController{

	/**
	 * Instance variables
	 */
	private Dice dice;
	private Board board;
	private TextBox textBox;
	private PlayerController playerController;
	private EventsController eventController;
	
	private int turnNumber;	// Used for debugging
	
	/** Boolean variable to indicate if the players have been instantiated with their name */
	private boolean playersNotInstantiated;
	
	/** The degree of rotation of the board */
	double rotation;
	
	/**
	 * Default constructor that initialize 
	 */
	protected GameController() {
	
		//sameErrorInRow = false;	
		turnNumber = 0;
		
		playersNotInstantiated = true;
		rotation = 0;
		board = new Board();
		textBox = new TextBox();
		dice = new Dice();
		playerController = new PlayerController();
		eventController = new EventsController();
		initUIListener();
		initGame();
	}
	
	private void initGame() {
		onGameStart();
		// runGame();
	}
	
	// ----- Game States -----
	
	/**
	 * Game start method
	 */
	public void onGameStart() {
		
		if(eventController.getTurnCount() == 0){   
			
			textBox.disableDiceRollBtn(true);
			textBox.output(eventController.promptPlayerForName());
			
			if(eventController.initializedPlayer == 2) {
				eventController.setTurnCount();
			}
			
		}else{
			
			if(playersNotInstantiated) { // Case to avoid beaver game play when deciding who is start the game
				textBox.output(dice.rollDice("turn"));
			}else
				textBox.output(dice.rollDice("move"));
			
			if (dice.compareTo() == 1) { // Second die is higher TODO
				changePerspective();
			}

			playersNotInstantiated = false;
			
			textBox.output(playerController.getCurrentPlayerName() + eventController.announceStartingPlayer());
			textBox.output(eventController.promptPlayerToMove());
		}
	}
	
	/**
	 * Method to convert labels on pip numbering when the player’s perspective changed
	 * @param pipIndex	int, the original pip index on the current board
	 * @return The new pip index after converted
	 */
	private int convertPipNumbering(int pipIndex) {
		return (23 - pipIndex);
	}
	
	/**
	 * Method to change the perspective of the players when turn changed
	 */
	private void changePerspective() {
		
		// System.out.println("Current Player : " + playerController.getCurrentPlayerName());
		playerController.changeCurrentPlayer();
		
		if (playerController.isColorEqual("white")) {
			rotation = 0;
		} else {
			rotation = 180;
		}
		
		// DEBUG 
		// System.out.println("Rotation: " + rotation );
		
		board.setPipLabelRegion(rotation);
		
		if(!playersNotInstantiated)
			eventController.setTurnCount(); 
		
		System.out.println("\tPerspective change\t\t: SUCCESS"); // Testing 
		
	}
	
	/**
	 * Display a pop-window that announce the end of game and the winner
	 * @param winner
	 */
	public void GameOver(String winner) {
		
		EndGame endGame = new EndGame(winner);
		getGameContainer().getChildren().clear();
		getGameContainer().getChildren().add(endGame.getEndGame_PopUp());
	}
	
	// ----- End of Game States -----
	
	// ----- Getter Methods -----
	
	/** Method to access the container that has the game GUI */
	public HBox getGameContainer() {
		return board.getGameContainer();
	}

	/** Method to access the text box container */
	public BorderPane getTextBox() {
		return textBox.getTextBox();
	}
	
	// ----- End of Getter Methods -----
	
	/**
	 * UI Listener instantiation 
	 */
	private void initUIListener() {
		
		// Handling the command typed in the input field
		textBox.getInputField().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER) {
					String text = textBox.getUserInput();
					String parts[] = text.split(" ");
					
					System.out.println("\n> Doing command " + turnNumber + " now"); // DEBUG

					if(!playersNotInstantiated)
						textBox.printUserInput(playerController.getCurrentPlayerName());
					
					/* ----- Command Cases ----- */
					
					/* If quitting */
					if (text.contains("quit")) {
						textBox.output("Exiting..");
						System.out.println("\tExit Game\t\t\t: SUCCESS\n");	// Testing
						System.exit(0);
					}

					
					/* Command to read all the available command and their format */
					else if(text.contains("help")) {
						textBox.displayHelp("all");
						
					}
					
					/* If it's a command or an ending turn */
					else if (parts[0].startsWith(".") || text.contains("next")) { 
						runCommand(parts);
					}

					/* Command to move any specific checkers to the given position (Testing) by ignoring all the rules */
					else if(text.contains("cheat")) {
						runCommand(parts);
					}
						
					/* Else just texting */
					else {
						textBox.output(text);
						System.out.println("\tNo command given but string");
					}
					
					textBox.clearInputField();
					System.out.println("\tCurrent turn number \t\t: " + eventController.getTurnCount());
					System.out.println("> Command " + turnNumber++ + " done"); // DEBUG
				}
			}
		});

		/* handling mouse click event to 'roll' / generate 2 random dice value */
		textBox.getDiceButton().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				textBox.output(dice.rollDice("move"));
				textBox.output(eventController.promptPlayerToMove());
				textBox.disableDiceRollBtn(true);
			}
		});
	}

	// ----- Command Executors -----
	
	/**
	 * Method to interpret what the user types into the command panel
	 * @param args	The break down of command input inserted on the text area
	 */
	private void runCommand(String[] args){

		/** User specific command expected here : .move to move the checker */
		String command = args[0];	

		/** First argument as a string passed from user command */
		String argv1 = ""; 
		
		/** Second argument as a string passed from user command */
		String argv2 = "";
		
		/** The pip index where a disk moved from */
		int moveFrom = -1;
		
		/** The pip index where the specified disk moved to */
		int moveTo   = -1;

		/**
		 * If user chooses to move
		 * -> Only when it is not in player instantiation stage
		 */
		if (command.contains(".move") || command.contains("cheat")) { //TODO
			
			if(playersNotInstantiated) { 			// Case to avoid the player to make disk move before instantiation 
				textBox.warningMessage("name");
				onGameStart();
				return;
			}
			
			/* String object that declared to store the command arguments given by the players in the text box */
			try{
				
				argv1 = args[1]; 	// expected an int , the index where the disks will be moved from
				argv2 = args[2];	// expected an int , the index where the disks will be moved to
				
				/* Convert user input string into integer */
				moveFrom = Integer.parseInt(argv1) - 1;
				moveTo   = Integer.parseInt(argv2) - 1;
				
				if(!(isWithinBounds(moveFrom)||isWithinBounds(moveTo))) {
					
					textBox.outputError("input");
					System.out.println("\tError message 1 created\t\t: SUCCESS [Pip Index Out of Range]"); // Testing
					return;
				}
				
			}catch(ArrayIndexOutOfBoundsException e) {	// Case for not enough command argument given
			
				textBox.outputError("command");
				textBox.output("Insufficient numbers of arguments given for the command above");
				System.out.println("\tError message 1A created\t\t: SUCCESS"); // Testing
				
				return;
			
			}catch(RuntimeException e) {				// Case for invalid argument given
				
				textBox.outputError("input");
				textBox.output("Invalid argument(s) format, integer expected");
				System.out.println("\tError message 1B created\t\t: SUCCESS [Argument Format Error]"); // Testing
				
				return;
				
			}finally{
				
				/*
			 	* 	This command ignores any game rules and even the given dice-roll value
			 	* 	but just move 1 checker which is in given pip index to any other pip
			 	*/
				if(command.contains("cheat")){ // TODO	
					commandCheat(moveFrom,moveTo);
				}
				
				/* Normal disk move : the player inserts the command to move checker(s) from 1 coordinate to another */
				else if(isLegalMove(Integer.parseInt(argv1) - 1, Integer.parseInt(argv2) - 1)!=0 && command.contains(".move")) { // TODO
					commandMove(moveFrom, moveTo);
				
				}else{
					
					textBox.outputError("move");
					System.out.println("\tError message 2 created\t\t: SUCCESS [Invalid Move Given]"); // Testing
					return;
				}
			}
		}	
		/* Command to instantiate the player's name or rename */
		else if(command.contains(".name")) {
			
			try {
				String currentName  = args[1];
				commandName(currentName);
				
			}catch(RuntimeException e) {	// try-catch invalid argument exception
				
				textBox.outputError("input");	
				textBox.output("Insufficient numbers of arguments given for command above");
				System.out.println("\tError message 3 created\t\t: SUCCESS [Naming Error] "); // Testing
				
				return;
			}
		}
		
		/* Command to switch the player in order to end the current game turn */
		else if(command.contains("next") && textBox.getDiceRollBtnDisabled()) {
			commandNext();
	
		}else{
			textBox.outputError("command");
			textBox.displayHelp("all");
			System.out.println("\tError message 5 created\t\t: SUCCESS"); // Testing
		}
		
		// create a block for each game turn 
		textBox.clearInputField();
	}
	
	/**
	 * Method to execute the functionality of valid disk moves, according to the given pip indices
	 * @param moveFrom	The pip index where the disk moved from
	 * @param moveTo	The pip index where the disk moved to
	 */
	public void commandMove(int moveFrom, int moveTo){
		
		if(rotation == 180) {
			moveFrom = convertPipNumbering(moveFrom);
			moveTo   = convertPipNumbering(moveTo);
		}
					
		board.moveDisks(moveFrom, moveTo);
					
		/* Display the remaining disk moves */
		if (dice.getNumberOfMoves() > 1) {
						
			textBox.output(dice.returnRemainingRolls(moveFrom, moveTo));
			System.out.println("\tRemaining disk move(s)\t\t: " + dice.getNumberOfMoves() + " moves");
						
		} /* End the current game round */ 
		else { 
			dice.restorePlayState(); // restore the dice roll play states -> normal play
			textBox.output(eventController.promptPlayerToEnterNext());
			eventController.setEndOfTurn(true);		
		}
	}
	
	/**
	 * Method to execute the functionality of disk move cheat, which the player can move any disk on the board to any pip preferred
	 * @param moveFrom	The pip index where the disk moved from
	 * @param moveTo	The pip index where the disk moved to
	 */
	public void commandCheat(int moveFrom, int moveTo){
				
		if(rotation == 180) { // change player perspective
		
			moveFrom = convertPipNumbering(moveFrom);
			moveTo   = convertPipNumbering(moveTo);
		}
				
		// move a specific checker to any pip without following any rules
		board.moveDisks(moveFrom, moveTo);	
				
		textBox.output(eventController.promptPlayerToEnterNext());
		System.out.println("\tCheating\t\t\t: SUCCESS"); 			// Testing
		// changePerspective();
		eventController.setEndOfTurn(true);
	}
	
	/**
	 * Method to initialize a player with a given name or rename the player in his/her game turn
	 * @param currentName	String that indicated as the name of the current player
	 */
	public void commandName(String name){
		
		if (playersNotInstantiated) { // Instantiation Case
			
			playerController.setCurrentPlayerName(name);
			textBox.output(playerController.displayCurrentPlayerInfo());
			changePerspective(); 
			System.out.println("\tInstantiate Current Player\t: SUCCESS");	// Testing
			onGameStart();
					
		}else{	// Renaming Case
									
			playerController.setCurrentPlayerName(name);
			textBox.output(playerController.displayCurrentPlayerInfo());
			System.out.println("\tRenaming Current Player\t: SUCCESS");	// Testing
			
		}
	}
	
	/**
	 * Method that do the execution of change the game turn and the current player
	 */
	public void commandNext(){
		
		if (playersNotInstantiated) {
					
			textBox.warningMessage("name");
			textBox.output(eventController.promptPlayerForName());
				
		} else if (eventController.isEndOfTurn()) {
					
			textBox.output("Your turn will now end..\n");  
					
			System.out.println(playerController.getCurrentPlayerColor()); // TODO
			changePerspective();
			textBox.output(playerController.getCurrentPlayerName() + eventController.promptPlayerToRollDice());
			textBox.disableDiceRollBtn(false);
			eventController.setEndOfTurn(false);
					
		} else {
			
			textBox.warningMessage("dice");
			textBox.output(eventController.promptPlayerToMove());
			System.out.println("\tError message 4 created\t\t: SUCCESS"); // Testing
		}
	}
	
	// ----- End of Command Executors -----
	
	// ----- Move Validator -----
	
	/**
	 * int method that check if the move instruction given is valid<br>
	 * - Pip Convertion when the player perspective changed<br>
	 * - Roll Dice first in game in order to get a new dice roll set value<br>
	 * - Check if the given pip indexes are valid <br>
	 * - Check if the move is mathematically valid<br>
	 * - Check if the pip is empty OR the disk(s) in the pip with index moveTo is same as the player's disk colour<br>
	 * @param moveFrom	The pip index where the checker is will move from
	 * @param moveTo	The pip index where the checker is will move to
	 * @return			0 if it is not a valid move, 1 if it is, and 2 if a valid hit off can be done 
	 */
	private int isLegalMove(int moveFrom, int moveTo) {
	
		if (rotation == 180) {
			moveFrom = convertPipNumbering(moveFrom);
			moveTo = convertPipNumbering(moveTo);
		}
		
		
		if(textBox.getDiceRollBtnDisabled() && eventController.getTurnCount() > 1) { 	// run .move command only when the dice roll has been played	
			textBox.warningMessage("roll");												// warning message : roll dice first
			return 0;
		}

		// Test if the given pip indexes are valid and if the move is mathematically valid
		if (isWithinBounds(moveFrom) && isWithinBounds(moveTo) && dice.isMoveAccordingToDiceRoll(moveFrom, moveTo)) {			
			
			System.out.println("\tPip Index Valid\t\t\t: SUCCESS");
			System.out.println("\tMove Given Valid\t\t\t: SUCCESS");
			
			try {
				// Test if the pip is empty OR the disk(s) in the pip with index moveTo is same as the player's disk colour
				if (board.getPipArray(moveTo).isEmpty() || playerController.isColorEqual(board.getDiskColorOnPip(moveFrom))) {	
					
					if(board.getPipArray(moveTo).isEmpty())
						System.out.println("\tPip moveFrom Not Empty\t\t: SUCCESS");
					else
						System.out.println("\tPip Color Meet\t\t\t: SUCCESS");
					// System.out.println("Dice: " + Math.abs(moveFrom - moveTo));
				
					return 1;
					
				} // Valid Disk Hit Case
				else if(board.getPipArray(moveTo).isBlot() && !board.getDiskColorOnPip(moveTo).equals(playerController.getCurrentPlayerColor())){  
					return 2;
				}
				
			}catch(StackEmptyException e) {	
				System.out.println("\tError message 6 created\t\t: SUCCESS"); // Testing
				textBox.outputError("input");
			}
		}
		return 0;  
	}
	
	/**
	 * Boolean method to check if the given pip index is in bound
	 * @param index	The given pip index
	 * @return	True if the given pip index is in bound ( 0 to 23 ), else false
	 */
	private boolean isWithinBounds(int index) {
		
		// DEBUG 
		// System.out.println("Index : " + index);
		
		if(index < 0 && index > 23) {
			textBox.output("Invalid move, try again");
			return false;
		}
		return true;
	}
	
	/* 
	 	getMapOfAllPossibleMoves is given a array of rolls (rolls that the player has left to use).
	  	for now: it will return a list containing all moves for each individual roll and 
	  	from the "total" roll number (from a certain pip position).
	  
	  	So basicly: it expects user to move one dice at a time, or use all dice for 1 disk move.
	  	TODO Beaver Case filled - Refactor may required
	 */
	/**
	 * Method that return a list, containing all moves for each individual roll and 
	 * from the "total" roll number
	 * Each array in the return ArrayList contains 3 elements : <br>
	 * int[0] - Starting position,<br>  
	 * int[1] - Ending position,<br> 
	 * int[2] - Move type (0 - normal move, 1 - hit move, 2 - board-entering move, 3 - bear-off move)<br>
	 * @return The ArrayList with all the integer arrays that indicates the valid moves
	 */
	public ArrayList<int[]> getMapOfAllPossibleMoves(){
		
		// Different playing states -> jail and normal move
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		
		/* 
		 * Case 1 : If the current player has checker(s) in jail
		 * - must roll pieces out of jail, then only normal move can be made
		 * - else end current game turn (no valid move to get out from jail)
		 */
		if(playerController.currentPlayerDiskInJail()) {
			
			// so: must check the "enemy home quarter" of the current player and the positions there that are free,
			// and must check if can "escape jail"
			// int value 24 is used to indicate stack in jail
			
			int sumDiceRoll = 0;
			
			for (int roll_individual : dice.getDiceRollSet()){

				sumDiceRoll += roll_individual;
				
				// Check for valid board-entering move that can be done by using 1 of the dice roll value
				if(board.getPipArray(roll_individual).isEmpty() || board.getDiskColorOnPip(roll_individual).equals(playerController.getCurrentPlayerColor())) {
					
					int validMove[] = { 24 , roll_individual , 2 };
					if(!doesMovePossibilityAlreadyExist(possibleMoves, validMove))
						possibleMoves.add(validMove);
				}	
				
				// Check for valid board-entering move that can be done by using the sum of the dice roll value
				if((board.getPipArray(sumDiceRoll).isEmpty() || board.getDiskColorOnPip(sumDiceRoll).equals(playerController.getCurrentPlayerColor()))&& sumDiceRoll != roll_individual) {
					
					int validMove[] = { 24 , sumDiceRoll , 2 }; 
					if(!doesMovePossibilityAlreadyExist(possibleMoves, validMove))
						possibleMoves.add(validMove);	
				}
			}		
			
		}else { /* Case when the current player has checker in jail but no valid move to enter the game board */ 
			textBox.output(playerController.getCurrentPlayerName() + " , you have no valid disk move in current game turn");
			changePerspective(); // change player turns
			return null;
		}
		
		/* 
		 * Case 2 : Normal Move & Checker Hit cases
		 * - Normal move can be made 
		 * - Checker hit move can be made
		 */
			
		// Iterate through board pips that the current player owns, 
		// and check what moves are possible with current dice rolls
		int currentPipPosition = 23;
		while(currentPipPosition >= 0){

			/* Sum of the dice roll value */
			int totalRoll = 0;
			
			/* Pip index that the current checker is going to move to */
			int positionAreMovingTo;
			
			/* The return value of isLegalMove() method that indicate the move type */
			int checkIfLegalMove; 
			int moveType = -1;
			
			for (int roll_individual : dice.getDiceRollSet()) {
			
				// ----- Check each individual roll for the pip -----
				
				positionAreMovingTo = currentPipPosition - roll_individual;
				checkIfLegalMove = isLegalMove(currentPipPosition, positionAreMovingTo);
				
				// Case 1 : Legal normal move
				if(checkIfLegalMove == 1)  
					moveType = 0;
				
				// Case 2 : Legal hit move
				else if(checkIfLegalMove == 2) 
					moveType = 1;
				
				if(checkIfLegalMove == 0) {
					continue;
				}else {
					int[] legalMove = {currentPipPosition,positionAreMovingTo,moveType};

					// Else the move already exists or 
					if(!doesMovePossibilityAlreadyExist(possibleMoves, legalMove)) 
						possibleMoves.add(legalMove);
				}
				
				// ----- End of checking individual roll value -----
				
				totalRoll += roll_individual;
				
				// ----- Check disk move with different combination sum of dice rolls -----
				
				positionAreMovingTo = currentPipPosition - totalRoll;
				checkIfLegalMove = isLegalMove(currentPipPosition, positionAreMovingTo);
				
				// Case 1 : Legal normal move
				if(checkIfLegalMove == 1)  
					moveType = 0;
				
				// Case 2 : Legal hit move
				else if(checkIfLegalMove == 2) 
					moveType = 1;
				
				if(checkIfLegalMove == 0) {
					continue;
				}else {
					int[] legalMove = {currentPipPosition,positionAreMovingTo,moveType};

					// Else the move already exists or 
					if(!doesMovePossibilityAlreadyExist(possibleMoves, legalMove)) 
						possibleMoves.add(legalMove);
				}
				
				// ----- End of checking disk move with different combination sum of dice rolls -----
			}
			currentPipPosition--;
		}	

		if(possibleMoves.isEmpty()) {
			// No moves are possible		(I will make an exception)
			textBox.output(playerController.getCurrentPlayerName() + " , you have no valid disk move in current game turn");
			changePerspective();	 // change player turns
			return null;
		}
		
		return possibleMoves;
	}
	
	private String[] getStringsForMapOfMoves(ArrayList<int[]> allMoves) {
		// all moves must actually contain moves (ie it must actually be possible for player to move before this method is called)
		int isMove = 0;
		int isAttack = 1;
		
		String[] listofMoves = new String[allMoves.size()];
		int indexInMovesList = 0;
		for (int[] move : allMoves) {
			
			listofMoves[indexInMovesList] = getStringOfMove(move);
			
			indexInMovesList++;
		}
		
		return listofMoves;
	}
	
	private String getStringOfMove(int[] theMove) {
		// Convert move to string
		int isMove = 0;
		int isAttack = 1;
		int isLeavingJail = 2;
		int isEnteringHome = 3;
		
		String moveAsString = "";
		if(theMove[2] == isLeavingJail) {
			// Bar off
			moveAsString += "bar-" + theMove[1];	// bar-finnishPosition
		}
		else {
			if(theMove[2] == isEnteringHome) {
				// player move(possible) piece to home
				moveAsString += theMove[0] + "-off";
			}
			else {
				// Is a move or a attack
				if(theMove[2] == isAttack) {
					// is attack
					moveAsString += theMove[0] + "-*" + theMove[1];
				}
				else {
					// is a move
					moveAsString += theMove[0] + "-" + theMove[1];
				}
			}
		}
		return moveAsString;
	}
	
	public boolean doesMovePossibilityAlreadyExist(ArrayList<int[]> listOfPossibleMoves, int[] theMove) {
		if(listOfPossibleMoves.contains(theMove)) {
			return true;
		}
		return false;
	}
}

package game;

import java.util.ArrayList;

import data_structures.StackEmptyException;
import gui.Board;
import gui.EndGame;
import gui.TextBox;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;

/**
 * Class that consists of operations that control the flow of the backgammon game
 *
 * @author Ee En Goh - 17202691
 * @version This class will control the logistics of the game, and not the mechanics. To be finished
 */
public class GameController{

	// VARIALES

	// INSTANCE VARIABLES
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

	// FUNCTION VARIABLES for commands
	private boolean playerWantsToQuite = false;
	
	private ArrayList<int[]> CurrentPlayersPossibleMoves;
	
	private boolean playerIsReadyToMakeMove = false;



	// END OF VARIABLES




	// SET UP METHODS

	/**
	 * Default constructor that initialize
	 */
	protected GameController(Rectangle2D screenBounds) {
		
		//sameErrorInRow = false;
		turnNumber = 0;

		playersNotInstantiated = true;
		rotation = 0;
		board = new Board(screenBounds);
		textBox = new TextBox(screenBounds.getHeight());
		dice = new Dice();
		playerController = new PlayerController();
		eventController = new EventsController();
		initUIListener();
		initGame();

		//GameOver("the winner");
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

		
		if(eventController.getTurnCount() == 0){	// Assign the players their names
			
			System.out.println("move 1");

			textBox.disableDiceRollBtn(true);
			textBox.output(eventController.promptPlayerForName());

			if(eventController.initializedPlayer == 2) {
				eventController.setTurnCount();
			}

		}else{		// Roll the dice and choose perspective

			/*
			if(playersNotInstantiated) { // Case to avoid beaver game play when deciding who is start the game
				textBox.output(dice.rollDice("turn"));
			}else
				textBox.output(dice.rollDice("move"));
				*/
			textBox.output(dice.rollDice("turn"));

			if (dice.compareTo() == 1) { // Second die is higher then first, so white will go first (so change perspective);
				changePerspective();
			}

			playersNotInstantiated = false;

			textBox.output(playerController.getCurrentPlayerName() + eventController.announceStartingPlayer());
			commandNext();
			
		}
	}

	// END OF SET UP METHODS

	/**
	 * Method to convert labels on pip numbering when the player’s perspective changed
	 * @param pipIndex	int, the original pip index on the current board
	 * @return The new pip index after converted
	 */
	private int convertPipNumbering(int pipIndex) {
		if(rotation == 0) {
			return pipIndex;
		}
		else {
			return (23 - pipIndex);
		}
		
	}

	/**
	 * Method to change the perspective of the players when turn changed
	 */
	private void changePerspective() {

		// System.out.println("Current Player : " + playerController.getCurrentPlayerName());
		playerController.changeCurrentPlayer();

		if (playerController.isCurrentPlayerEqual("white")) {
			rotation = 0;
		} else {
			rotation = 180;
		}

		// DEBUG
		// System.out.println("Rotation: " + rotation );

		board.setPipLabelRegion(rotation);

		if(!playersNotInstantiated)
			eventController.setTurnCount();

		System.out.println("\tPerspective change\t\t: SUCCESS " + rotation); // Testing

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
						System.out.println("cheat command called");
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
				
				// Tell player the moves
				playerIsReadyToMakeMove = true;

				GetCurrentMoves();	// Get the moves, and print them

				
			}
		});
	}
	
	private void GetCurrentMoves() {
		CurrentPlayersPossibleMoves = getMapOfAllPossibleMoves();
		
		System.out.println("printing out moves");
		/*
		for(int i = 0; i < CurrentPlayersPossibleMoves.size();i++) {
			System.out.println(CurrentPlayersPossibleMoves.get(i).toString());
		}
		*/
		
		System.out.println("Current move possiblities have been gotten");
		
		if(CurrentPlayersPossibleMoves != null) {
			// print all the moves
			textBox.printPossibleMoves(getStringsForMapOfMoves(CurrentPlayersPossibleMoves));
			
			// Prompt player to make move
			textBox.output(eventController.promptPlayerToMove());
		}
		
		else {
			// Player cant make a move and must end turn
			textBox.output("no moves availible");
			endTurn();
		}
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
		
		if(command.contains(".rotation")) {
			textBox.output("the rotation is " + rotation);
		}

		/**
		 * If user chooses to move
		 * -> Only when it is not in player instantiation stage
		 */
		else if (command.contains(".move")) { 
			
			if(playersNotInstantiated) { 			// Case to avoid the player to make disk move before instantiation 
				textBox.warningMessage("name");
				onGameStart();
				return;
			}
			
			if(playerIsReadyToMakeMove) {
				// Player is ready to make move
				/* String object that declared to store the command arguments given by the players in the text box */
				try{
					
					argv1 = args[1]; 	// expected an int , the index where the disks will be moved from
					
					/* Convert user input string into integer */
					int moveSelected = Integer.parseInt(argv1);
					
					int[] currentMove = CurrentPlayersPossibleMoves.get(moveSelected);
					
					// try extract the moves details 
					if(currentMove == null) {
						// Move does not exist
						textBox.outputError("input");
						System.out.println("\tError message 1 created\t\t: SUCCESS [Pip Index Out of Range]"); // Testing
						return;
					}
					else {
						// move exists
						// try and use the move
						if(currentMove[2] == 0) {
							// Move is: normal move
							textBox.output("you have selected move :" + getStringOfMove(currentMove));
							commandMove(currentMove[0] - 1,currentMove[1] - 1);	// Move the piece
							
						}
						
						else if(currentMove[2] == 1) {
							// Move is: hit
							textBox.output("you have selected attack :" + getStringOfMove(currentMove));
							commandAttack(currentMove[0] - 1,currentMove[1] - 1);

						}
						
						else if(currentMove[2] == 2) {
							// Move is: Leaving jail
							textBox.output("you have selected to leave jail :" + getStringOfMove(currentMove));
							commandJailLeave(currentMove[1] - 1);
							
						}
						
						else if(currentMove[2] == 3) {
							// Move is: Bear off
						}
						
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
					
					/* Normal disk move : the player inserts the command to move checker(s) from 1 coordinate to another */
						
					textBox.outputError("move");
					System.out.println("\tError message 2 created\t\t: SUCCESS [Invalid Move Given]"); // Testing
					return;
				}
			}	
			else {
				// not ready to make move
				textBox.outputError("you still need to roll");
			}
		}
		
		else if(command.contains("cheat")){ 
			System.out.println("help");
			try {
				argv1 = args[1];
				argv2 = args[2];
				int moveFrom = Integer.parseInt(argv1);
				int moveTo = Integer.parseInt(argv2);
				commandCheat(moveFrom,moveTo);
				
				endTurn();
			}catch (Exception e) {
				System.out.println("Cheat command did not get required arguments");
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
		
		// command to check current player
		else if (command.contains(".currentPlayer")) {
			textBox.output("Current player is: " + playerController.getCurrentPlayerName() + "   " + playerController.getCurrentPlayerColor());
		}
		
		else{
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
	private void commandMove(int moveFrom, int moveTo){
		System.out.println("The dice is " + (moveFrom - moveTo));
		String remainingMoves = dice.returnRemainingRolls(moveFrom-moveTo);
		
		moveFrom = convertPipNumbering(moveFrom);
		moveTo   = convertPipNumbering(moveTo);
		
		
		board.moveDisks(moveFrom, moveTo);


		/* Display the remaining disk moves */
		if (dice.getNumberOfDiceLeft() >= 1) {

			textBox.output(remainingMoves);		// Remove the dice used
			System.out.println("\tRemaining disk move(s)\t\t:" + dice.getNumberOfDiceLeft() + " moves");
			
			GetCurrentMoves();	// Get remaining moves

		} /* End the current game round */
		else {
			dice.restorePlayState(); // restore the dice roll play states -> normal play
			endTurn();
		}
	}
	
	private void commandAttack(int moveFrom, int attackPosition) {
		
		String remainingMoves = dice.returnRemainingRolls(Math.abs(moveFrom-attackPosition));
		
		moveFrom = convertPipNumbering(moveFrom);
		attackPosition   = convertPipNumbering(attackPosition);
		
		board.successHit(moveFrom, attackPosition);
		
		playerController.setEnemyPlayerInJail();	// mark that the enemy player is in the jail now
				
		/* Display the remaining disk moves */
		if (dice.getNumberOfDiceLeft() >= 1) {
			// Still have moves left
			textBox.output(remainingMoves);		// Remove the dice used
			System.out.println("\tRemaining disk move(s)\t\t: " + dice.getNumberOfDiceLeft() + " moves");
			
			GetCurrentMoves();	// Get remaining moves

		} /* End the current game round */
		else {
			// End of turn
			dice.restorePlayState(); // restore the dice roll play states -> normal play
			endTurn();
		}
		
		
	}
	
	private void commandJailLeave(int movePosition) {
		
		String remainingMoves = dice.returnRemainingRolls(Math.abs(movePosition+1));
		
		movePosition   = convertPipNumbering(movePosition);
		
		board.removeFromJail(movePosition);
		
		playerController.currentPlayerLeavesJail();	// mark that the enemy player is in the jail now
				
		/* Display the remaining disk moves */
		if (dice.getNumberOfDiceLeft() >= 1) {
			// Still have moves left
			textBox.output(remainingMoves);		// Remove the dice used
			System.out.println("\tRemaining disk move(s)\t\t: " + dice.getNumberOfDiceLeft() + " moves");
			
			GetCurrentMoves();	// Get remaining moves

		} /* End the current game round */
		else {
			// End of turn
			dice.restorePlayState(); // restore the dice roll play states -> normal play
			endTurn();
		}
		
	}

	/**
	 * Method to execute the functionality of disk move cheat, which the player can move any disk on the board to any pip preferred
	 * @param moveFrom	The pip index where the disk moved from
	 * @param moveTo	The pip index where the disk moved to
	 */
	public void commandCheat(int moveFrom, int moveTo){
		System.out.println("commandCheat being called");
		moveFrom--;
		moveTo--;

		moveFrom = convertPipNumbering(moveFrom);
		moveTo   = convertPipNumbering(moveTo);

		// move a specific checker to any pip without following any rules
		board.moveDisks(moveFrom, moveTo);

		System.out.println("\tCheating\t\t\t: SUCCESS"); 			// Testing
		// changePerspective();
	}

	/**
	 * Method to initialize a player with a given name or rename the player in his/her game turn
	 * @param currentName	String that indicated as the name of the current player
	 */
	public void commandName(String name){

		if (playersNotInstantiated) { // Instantiation Case

			playerController.setCurrentPlayerName(name);
			textBox.output(playerController.displayCurrentPlayerInfo());
			changePerspective();	// so to ask other player
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

		} else {
			// Player prompted to take turn
			textBox.output(eventController.promptPlayerWithName(playerController.getCurrentPlayerColor()));
			instantiateCurrentPlayersMove();
			
		}
	}
	
	private void instantiateCurrentPlayersMove() {
		// gets current player (color) and give it the list of possible moves
		
		playerIsReadyToMakeMove = false;
				
		// Tell player to roll the dice
		textBox.output(eventController.promptPlayerToRollDice());
		
		textBox.disableDiceRollBtn(false);	// Set the button on	
		
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
		
		moveFrom = convertPipNumbering(moveFrom);
		moveTo = convertPipNumbering(moveTo);


			// Are ready to move as have rolled dice
			// Test if the given pip indexes are valid and if the move is mathematically valid
			if (isWithinBounds(moveFrom) && isWithinBounds(moveTo)) {

				System.out.println("\tPip Index Valid\t\t\t: SUCCESS");
				System.out.println("\tMove Given Valid\t\t\t: SUCCESS");

				try {
					// Test if the pip is empty OR the disk(s) in the pip with index moveTo is same as the player's disk colour
					if(board.checkIfCurrentPlayerOwnsPipPosition(moveFrom, playerController.getCurrentPlayerColor())) {
						// Own piece on point
						if(board.getPipArray(moveTo).isEmpty() || board.checkIfCurrentPlayerOwnsPipPosition(moveTo, playerController.getCurrentPlayerColor())) {
							// point are moveing to is empty or owned by current player, so can move there
							return 1;
						}
						else if(board.getPipArray(moveTo).isPointVulnerable()) {
							// able to attack enemy player point as it is vulnerable
							return 2;
						}
					}

				}catch(StackEmptyException e) {
					System.out.println("\tError message 6 created\t\t: SUCCESS"); // Testing
					textBox.outputError("input");
				}
			}
			return 0;

	}


	private void endTurn() {
		textBox.output("Your turn will now end..\n");

		changePerspective();		// Changes current player aswell
		textBox.output(playerController.getCurrentPlayerName() + eventController.promptPlayerToRollDice());
		textBox.disableDiceRollBtn(false);		// enabling
		
		commandNext();
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
		
		if(!playerIsReadyToMakeMove) { 	// run .move command only when the dice roll has been played
			textBox.warningMessage("roll");												// warning message : roll dice first
			return null;
		}

		/*
		 * Case 1 : If the current player has checker(s) in jail
		 * - must roll pieces out of jail, then only normal move can be made
		 * - else end current game turn (no valid move to get out from jail)
		 */
		if(playerController.isCurrentPlayerInJail()) {
			
			System.out.println("Current player is in jail");

			// so: must check the "enemy home quarter" of the current player and the positions there that are free,
			// and must check if can "escape jail"
			// int value 24 is used to indicate stack in jail
			
			// Can either use individual dice, or the sumr

			int sumDiceRoll = 0;

			for (int roll_individual : dice.getDiceRollSet()){

				sumDiceRoll += roll_individual;
				
				int position = 24-roll_individual;

				// Check for valid board-entering move that can be done by using 1 of the dice roll value
				if(position >= 18 && (board.getPipArray(convertPipNumbering(position)).isEmpty() || board.checkIfCurrentPlayerOwnsPipPosition(convertPipNumbering(position), playerController.getCurrentPlayerColor()))
						|| board.getPipArray(convertPipNumbering(position)).isPointVulnerable()){

					int validMove[] = { 24 , position + 1 , 2 };
					if(!doesMovePossibilityAlreadyExist(possibleMoves, validMove))
						possibleMoves.add(validMove);
				}
			}
			
			// Check for valid board-entering move that can be done by using the sum of the dice roll value
			int position = 24-sumDiceRoll;
			if(position >= 18 && (board.getPipArray(position).isEmpty() || board.getDiskColorOnPip(position).equals(playerController.getCurrentPlayerColor()))) {
					
				int validMove[] = { 24 , (position + 1) , 2 };
				if(!doesMovePossibilityAlreadyExist(possibleMoves, validMove))
					possibleMoves.add(validMove);
			}
			
			
		}
		else {
			System.out.println("player is not in jail");
			
			/*
			 * Case 2 : Normal Move & Checker Hit cases
			 * - Normal move can be made
			 * - Checker hit move can be made
			 */

			// Iterate through board pips that the current player owns,
			// and check what moves are possible with current dice rolls
			
			
			int numberOfDice = dice.getNumberOfDiceLeft();
			
			ArrayList<Integer> diceRolls = dice.getDiceRollSet();
			
			int currentPipPosition = 23;
			while(currentPipPosition >= 0){
				// if dont own position, then this if will be skipped
				System.out.println("lllllll");
				if(board.checkIfCurrentPlayerOwnsPipPosition(convertPipNumbering(currentPipPosition), playerController.getCurrentPlayerColor())) {
					// player owns the current pip position
					/* Sum of the dice roll value */
					System.out.println("board");

					/* Pip index that the current checker is going to move to */
					int positionAreMovingTo;

					/* The return value of isLegalMove() method that indicate the move type */
					int checkIfLegalMove;
					int moveType;
					
					for(int i = 0; i<numberOfDice;i++) {
					//for (int roll_individual : dice.getDiceRollSet()) {

						// ----- Check each individual roll for the pip -----

						positionAreMovingTo = currentPipPosition - diceRolls.get(i);
						if(positionAreMovingTo >= 0) {
							// moveing to place still within board
							checkIfLegalMove = isLegalMove(currentPipPosition, positionAreMovingTo);
							
							if(checkIfLegalMove != 0) {
								moveType = checkIfLegalMove-1;		// Assigneing move type
								
								int[] legalMove = {currentPipPosition + 1, positionAreMovingTo + 1, moveType};
								
								// Else the move already exists or
								if(!doesMovePossibilityAlreadyExist(possibleMoves, legalMove)) {
									possibleMoves.add(legalMove);
								}
							}
						}
						else {
							break;
						}
						// else are trying to move outside board limits
						


						// ----- End of checking individual roll value -----
						
						/*

						totalRoll += roll_individual;

						// ----- Check disk move with different combination sum of dice rolls -----

						positionAreMovingTo = currentPipPosition - totalRoll;
						checkIfLegalMove = isLegalMove(currentPipPosition, positionAreMovingTo);

						// If move is possible, set type of move
						if(checkIfLegalMove != 0) {
							moveType = checkIfLegalMove-1;

						
							int[] legalMove = {currentPipPosition,positionAreMovingTo,moveType};

							// Else the move already exists or
							if(!doesMovePossibilityAlreadyExist(possibleMoves, legalMove))
								possibleMoves.add(legalMove);
						}

						// ----- End of checking disk move with different combination sum of dice rolls -----
						*/
					}
				}


				currentPipPosition--;
			}	
		}



		if(possibleMoves.isEmpty()) {
			// No moves are possible		(I will make an exception)
			textBox.output(playerController.getCurrentPlayerName() + " , you have no valid disk move in current game turn");
			
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
		for (int i = 0; i < allMoves.size();i++) {

			listofMoves[i] = i + "-> " + getStringOfMove(allMoves.get(i));
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
		for (int[] move : listOfPossibleMoves) {
			if(move[0] == theMove[0] && move[1] == theMove[1] && move[2] == theMove[2]) {
				return true;
			}
		}
		return false;
	}
}

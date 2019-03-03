package game;

import data_structures.StackEmptyException;
import gui.Board;
import gui.TextBox;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Class that consists of operations that control the flow of the backgammon game
 * @author YeohB - 17357376
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
	}
	
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
				else if(isLegalMove(Integer.parseInt(argv1) - 1, Integer.parseInt(argv2) - 1) && command.contains(".move")) { // TODO
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
						
		} else { // Current game round end
						
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
	
	/**
	 * Boolean method that check if the move instruction given is valid
	 * @param moveFrom	The pip index where the checker is will move from
	 * @param moveTo	The pip index where the checker is will move to
	 * @return			True, if the given move is valid, else false
	 */
	private boolean isLegalMove(int moveFrom, int moveTo) {
		
		if (rotation == 180) {
			moveFrom = convertPipNumbering(moveFrom);
			moveTo = convertPipNumbering(moveTo);
		}
		
		
		if(textBox.getDiceRollBtnDisabled() && eventController.getTurnCount() > 1) { 	// run .move command only when the dice roll has been played	
			textBox.warningMessage("roll");												// warning message : roll dice first
			return false;
		}

		if (isWithinBounds(moveFrom) && isWithinBounds(moveTo)) {			// Test if the given pip indexes are valid
			System.out.println("\tPip Index Valid\t\t\t: SUCCESS");
	
			try {
				// Test if the pip is empty OR the disk(s) in the pip with index moveTo is same as the player's disk colour
				if (!board.getPipArray(moveFrom).isEmpty() || playerController.isColorEqual(board.getDiskColorOnPip(moveFrom))) {	
				
					if(!board.getPipArray(moveFrom).isEmpty()) {
						System.out.println("\tPip Not Empty\t\t\t: SUCCESS");
					}else
						System.out.println("\tPip Color Meet\t\t\t: SUCCESS");
					// System.out.println("Dice: " + Math.abs(moveFrom - moveTo));
				
					if (dice.isMoveAccordingToDiceRoll(moveFrom, moveTo)) {		// Test if the move is mathematically valid
						System.out.println("\tDice Roll Valid\t\t\t: SUCCESS");
						return true;
					}
				}	
				
			}catch(StackEmptyException e) {	
				System.out.println("\tError message 6 created\t\t: SUCCESS"); // Testing
				textBox.outputError("input");
			}
		}
		return false;  
	}

	/**
	 * Method to change the perspective of the players when turn changed
	 */
	private void changePerspective() {
		// TODO DEBUG
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
			eventController.setTurnCount(); // TODO
		
		System.out.println("\tPerspective change\t\t: SUCCESS"); // Testing 
		
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
	
	/**
	 * Method to convert labels on pip numbering when the player’s perspective changed
	 * @param pipIndex	int, the original pip index on the current board
	 * @return The new pip index after converted
	 */
	private int convertPipNumbering(int pipIndex) {
		
		return (23 - pipIndex);
	}
	
	/** Method to access the container that has the game GUI */
	public HBox getGameContainer() {
		return board.getGameContainer();
	}

	/** Method to access the text box container */
	public BorderPane getTextBox() {
		return textBox.getTextBox();
	}
}

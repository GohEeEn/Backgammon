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
	//private boolean sameErrorInRow;
	
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
		
		if(eventController.getTurnCount() < 2){   
			textBox.disableDiceRollBtn(true);
			textBox.output(eventController.promptPlayerForName());
			
		}else{
			
			if(playersNotInstantiated) // Case to avoid beaver game play when deciding who is start the game
				textBox.output(dice.rollDice("turn"));
			else
				textBox.output(dice.rollDice("move"));
			
			playersNotInstantiated = false;
			
			if (dice.compareTo() == 1) { // Second die is higher
				changePerspective();
			}

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
					
					System.out.println("\n> Doing Turn " + turnNumber + " now"); // DEBUG

					if(!playersNotInstantiated)
						textBox.printUserInput(playerController.getCurrentPlayerName());
					
					/* ----- Command Cases ----- */
					
					/* If quitting */
					if (text.contains("quit")) {
						textBox.output("Exiting..");
						System.out.println("\tExit Game\t\t\t: SUCCESS\n");	// Testing
						System.exit(0);
					}

					/* If ending turn */
					else if (text.contains("next")) {
						runCommand(parts);
					}
					
					/* Command to read all the available command and their format */
					else if(text.contains("help")) {
						textBox.displayHelp("all");
						
					}
					
					/* If it's a command */
					else if (parts[0].startsWith(".")) { 
						runCommand(parts);
					}

					/* Command to move any specific checkers to the given position (Testing) by ignoring all the rules */
					else if(text.contains("cheat")) {
						runCommand(parts);
					}
						
					/* Else just texting */
					else {
						textBox.output(text);
						turnNumber--;
					}
					
					textBox.clearInputField();
					System.out.println("> Turn " + turnNumber++ + " done"); // DEBUG
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

		/** User command expected here : .move to move the checker */
		String command = args[0];	

		int moveFrom = -1;
		int moveTo   = -1;

		/**
		 * If user chooses to move
		 * -> Only when it is not in player instantiation stage
		 */
		if (command.contains(".move") || command.contains("cheat")) { //TODO
			
			if(playersNotInstantiated) { 			// Case to avoid the player to make disk move before instantiation 
				//if(!sameErrorInRow) {
					textBox.warningMessage("name");
				//	sameErrorInRow = true;
				//	return;
				//}
				 	onGameStart();
				
				return;
			}
			
		
			/* String object that declared to store the command arguments given by the players in the text box */
			String argv1, argv2;
		
			try{
			
				argv1 = args[1]; 	// expected an int , the index where the disks will be moved from
				argv2 = args[2];	// expected an int , the index where the disks will be moved to
		
			}catch(ArrayIndexOutOfBoundsException e) {
			
				textBox.outputError("command");
				textBox.output("Insufficient numbers of arguments given for the command above");
				return;
			}
			//	System.out.println("argv : " + argv1 + " " + argv2); DEBUG
			
			/**
			 * NOTE: The implementation of "jail" is only to demonstrate the ability to move
			 * to/from the jail. The user will not be able to choose to move to the jail in
			 * the final game
			 */

			// ( Must to move the checker(s) in the jail out from it if the current player has this situation, otherwise skip round )
			
			/* If user wants to move to the jail */
			if (argv2.contains("jail")) {			
				
				/* Call function in board to add/remove disks */
				if (isWithinBounds(Integer.parseInt(argv1) - 1)) {

					moveFrom = Integer.parseInt(argv1) - 1;

					board.getJail().push(board.getPipArray(moveFrom).updatePoppedDisks());
					board.getJail().updateJail();
				}
			}

			/* If user wants to move from the jail */
			else if (argv1.contains("jail")) {
				
				if (isWithinBounds(Integer.parseInt(argv2) - 1)) {
					System.out.println("Enter function");
					moveTo = Integer.parseInt(argv2) - 1;
					
					board.getPipArray(moveTo).updatePushedDisks(board.getJail().pop());
					board.getJail().updateJail();
				}
			}
			
			/* Normal disk move : the player inserts the command to move checker(s) from 1 coordinate to another */
			else if(isLegalMove(Integer.parseInt(argv1) - 1, Integer.parseInt(argv2) - 1) && command.contains(".move")) { // TODO
				
				moveFrom = Integer.parseInt(argv1) - 1;
				moveTo   = Integer.parseInt(argv2) - 1;
				
				if(rotation == 180) {
					moveFrom = convertPipNumbering(moveFrom);
					moveTo   = convertPipNumbering(moveTo);
				}
				
				board.moveDisks(moveFrom, moveTo);
				
				/* Display the remaining disk moves */
				if (dice.getNumberOfMoves() > 1) {
					
					textBox.output(dice.returnRemainingRolls(moveFrom, moveTo));
					System.out.println(dice.getNumberOfMoves());
					
				} else { // Current game round end
					
					textBox.output(eventController.promptPlayerToEnterNext());
					eventController.setEndOfTurn(true);
					
				}
				
			}else if(command.contains("cheat")){ // TODO
				
				/*
				 * 	This command ignores any game rules and even the given dice-roll value
				 * 	but just move 1 checker which is in given pip index to any other pip
				 */
				
				moveFrom = Integer.parseInt(argv1) - 1;
				moveTo   = Integer.parseInt(argv2) - 1;
				
				if(rotation == 180) { // change player perspective
					
					moveFrom = convertPipNumbering(moveFrom);
					moveTo   = convertPipNumbering(moveTo);
				}
				
				// move a specific checker to any pip without following any rules
				board.moveDisks(moveFrom, moveTo);	
				
				changePerspective();
				textBox.output(eventController.promptPlayerToEnterNext());
				System.out.println("\tCheating\t\t\t: SUCCESS"); // Testing
				eventController.setEndOfTurn(true);
				
			}else{
				textBox.outputError("move");
			}
		}
		
		/* Command to instantiate the player's name or rename */
		else if(command.contains(".name")) {
			
			try {
				// sameErrorInRow = false;			// Invert case used to oppose naming command error
				String currentName  = args[1];
			
				if (playersNotInstantiated) {
					playerController.setCurrentPlayerName(currentName);
					textBox.output(playerController.displayCurrentPlayerInfo());
					changePerspective(); 
					System.out.println("\tInstantiate Current Player\t: SUCCESS");	// Testing
					onGameStart();
				
				}else{
					
					System.out.println("\tRenaming Current Player\t: SUCCESS");	// Testing
					
					String newName = args[2];
					if (currentName.compareTo(playerController.getCurrentPlayerName()) == 0) {
						playerController.setCurrentPlayerName(newName);
						textBox.output(playerController.displayCurrentPlayerInfo());
					} else {
						System.out.println("\tRenaming Current Player\t: FAIL");	// Testing
						textBox.outputError("input");
						textBox.displayHelp(".name");
					}
				}
				
			}catch(ArrayIndexOutOfBoundsException e) {
				
				textBox.outputError("command");
				textBox.output("Insufficient numbers of arguments given for command above");
				return;
			}
		}
		
		/* Command to switch the player in order to end the current game turn */
		else if(command.contains("next")) {
			
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
				
				//if(!sameErrorInRow) {
					textBox.outputError("input");
					textBox.warningMessage("dice");
				//	sameErrorInRow = true;
				//	return;
				//}
				textBox.output(eventController.promptPlayerToMove());
			}
			
		}else{
			textBox.outputError("command");
			textBox.displayHelp("all");
		}
		
		// create a block for each game turn 
		textBox.clearInputField();
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
				System.out.println("\tWarning : The pip is empty");
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
		
		// TODO DEBUG 
		// System.out.println("Rotation: " + rotation );
		
		board.setPipLabelRegion(rotation);
		eventController.setTurnCount();
		
		// TODO DEBUG
		// System.out.println("Perspective change successful");
		
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
		switch (pipIndex) {
		case 0:
			pipIndex = 23;
			break;
		case 1:
			pipIndex = 22;
			break;
		case 2:
			pipIndex = 21;
			break;
		case 3:
			pipIndex = 20;
			break;
		case 4:
			pipIndex = 19;
			break;
		case 5:
			pipIndex = 18;
			break;
		case 6:
			pipIndex = 17;
			break;
		case 7:
			pipIndex = 16;
			break;
		case 8:
			pipIndex = 15;
			break;
		case 9:
			pipIndex = 14;
			break;
		case 10:
			pipIndex = 13;
			break;
		case 11:
			pipIndex = 12;
			break;
		case 12:
			pipIndex = 11;
			break;
		case 13:
			pipIndex = 10;
			break;
		case 14:
			pipIndex = 9;
			break;
		case 15:
			pipIndex = 8;
			break;
		case 16:
			pipIndex = 7;
			break;
		case 17:
			pipIndex = 6;
			break;
		case 18:
			pipIndex = 5;
			break;
		case 19:
			pipIndex = 4;
			break;
		case 20:
			pipIndex = 3;
			break;
		case 21:
			pipIndex = 2;
			break;
		case 22:
			pipIndex = 1;
			break;
		case 23:
			pipIndex = 0;
			break;
		}

		return pipIndex;
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

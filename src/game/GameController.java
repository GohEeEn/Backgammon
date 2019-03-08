package game;

import java.util.ArrayList;

import com.sun.javafx.collections.MappingChange.Map;

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
 * @author YeohB - 17357376
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
	
	/** Boolean variable to indicate if the players have been instantiated with their name */
	private boolean playersNotInstantiated;
	
	/** The degree of rotation of the board */
	double rotation;
	//private boolean sameErrorInRow;
	
	// FUNCTION VARIABLES (TO "CATCH" COMMANDS)
	private static boolean t;
	
	
	// END OF VARIABLES
	
	
	
	
	// SET UP METHODS
	
	/**
	 * Default constructor that initialize 
	 */
	protected GameController() {
		playersNotInstantiated = true;
		//sameErrorInRow = false;	// TODO
		rotation = 0;
		board = new Board();
		textBox = new TextBox();
		dice = new Dice();
		playerController = new PlayerController();
		eventController = new EventsController();
		initUIListener();
		initGame();
		
		//GameOver("the winner");
	}
	
	private void initGame() {
		onGameStart();
		
		runGame();
	}
	
	/**
	 * Game start method
	 */
	public void onGameStart() {
		
		if(eventController.getTurnCount() < 2){ // TODO : Different version, my turnCount starts from 0  
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
	
	// run the game after the players have been initialised
	public void runGame() { 
		
		while() {
			String  currentPlayer = playerController.getCurrentPlayerColor();
		}
		
	}
	
	// END OF SET UP METHODS
	
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

					if(!playersNotInstantiated)
						textBox.printUserInput(playerController.getCurrentPlayerName());
					
					/* If it's a command */
					if (parts[0].startsWith(".")) { 
						runCommand(parts);
					}

					/* If quitting */
					else if (text.contains("quit")) {
						textBox.output("Exiting..");
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
					/* Else just texting */
					else {
						textBox.output(text);
					}
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
		if (command.contains(".move")) {
			
			if(playersNotInstantiated) { 			// Case to avoid the player to make disk move before instantiation 
				System.out.println("Entered");
				
				//if(!sameErrorInRow) {
					textBox.warningMessage("name");
				//	sameErrorInRow = true;
				//	return;
				//}
					textBox.clearInputField();
				 	onGameStart();
				
				return;
			}
			
		
		/* String object that declared to store the command arguments given by the players in the text box */
		String argv1, argv2;
		
		try{
			
			argv1 = args[1]; 		
			argv2 = args[2];
		
		}catch(ArrayIndexOutOfBoundsException e) {
			
			textBox.outputError("command");
			textBox.output("Insufficient numbers of arguments given for this command");
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
			
			else if(true) {
				
				moveFrom = Integer.parseInt(argv1) - 1;
				moveTo   = Integer.parseInt(argv2) - 1;
				
				if(rotation == 180) {
					moveFrom = convertPipNumbering(moveFrom);
					moveTo   = convertPipNumbering(moveTo);
				}
				
				board.movePiece(moveFrom, moveTo);
				
				/* Display the remaining disk moves */
				if (dice.getNumberOfMoves() > 1) {
					
					textBox.output(dice.returnRemainingRolls(moveFrom, moveTo));	// TODO
					System.out.println(dice.getNumberOfMoves());
					
				} else { // Current game round end
					
					textBox.output(eventController.promptPlayerToEnterNext());
					eventController.setEndOfTurn(true);
					
				}
				
			}else{
				
			//	if(!sameErrorInRow) {
					textBox.outputError("move");
			//		sameErrorInRow = true;
			//		return;
			//	}
				textBox.clearInputField();
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
					changePerspective(); // TODO
					onGameStart();
				}else {
					System.out.println("Rename Entered");
					String newName = args[2];
					if (currentName.compareTo(playerController.getCurrentPlayerName()) == 0) {
						playerController.setCurrentPlayerName(newName);
						textBox.output(playerController.displayCurrentPlayerInfo());
					} else {
						textBox.outputError("input");
						textBox.displayHelp(".name");
					}
				}
				
			}catch(ArrayIndexOutOfBoundsException e) {
				
				textBox.outputError("command");
				textBox.output("Insufficient numbers of arguments given for this command");
				return;
			}
		}
		
		/* Command to switch the player in order to end the current game turn */
		else if(command.contains("next")) {
			
			if (playersNotInstantiated) {
				
				textBox.warningMessage("name");
				textBox.output(eventController.promptPlayerForName());
			
			} else if (eventController.isEndOfTurn()) {
				
				textBox.output("Your turn will now end.."); 
				System.out.println(playerController.getCurrentPlayerColor());
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
		textBox.clearInputField();
	}

	/**
	 * int method that check if the move instruction given is valid
	 * @param moveFrom	The pip index where the checker is will move from
	 * @param moveTo	The pip index where the checker is will move to
	 * @return			0 if is not a valid move, 1 if can move, and 2 if can take an enemy piece
	 */
	
	private int isLegalMove(int moveFrom, int moveTo) {
		
		if (rotation == 180) {
			moveFrom = convertPipNumbering(moveFrom);
			moveTo = convertPipNumbering(moveTo);
		}

		if (isWithinBounds(moveFrom) && isWithinBounds(moveTo)) {			// Test if the given pip indexes are valid
			System.out.println("Range: SUCCESS");
			if(board.getPipArray(moveFrom).isEmpty()) {
				// No piece on the pip
				return 0;
			}
			else if(playerController.isColorEqual(board.getDiskColorOnPip(moveFrom))) {
				if(playerController.isColorEqual(board.getDiskColorOnPip(moveTo))) {
					// Own pip that are moving to, so can move there
					return 1;
				}
				else {
					// is enemy pip
					if(board.getPipArray(moveTo).size() == 1) {
						// Only 1 enemy piece on pip, so can take pip
						return 2;
					}
					else {
						// too many enemy pieces on the pip
						return 0;
					}
				}
			}
		}
		return 0;
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
	
	public void GameOver(String winner) {
		
		EndGame endOfGame_Window = new EndGame(winner);
		getGameContainer().getChildren().clear();
		getGameContainer().getChildren().add(endOfGame_Window.getEndGame_PopUp());
		
	}
	
	/* getMapOfAllPossibleMoves is given a array of rolls (rolls that the player has left to use).
	  for now: it will return a list containing all moves for each individual roll and 
	  from the "total" roll number (from a certain pip position).
	  
	  So basicly: it expects user to move one dice at a time, or use all dice for 1 pip.
	 */
	public ArrayList<int[]> getMapOfAllPossibleMoves(int[] rolls){
		// Different playing states -> jail and normal move
		ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
		
		if(playerController.isCurrentPlayerInJail()) {
			// Then the current player is in jail, and must roll pieces out of jail
			// so: must check the "enemy home quarter" of the current player and the positions there that are free,
			// and must check if can "escape jail"
			
			
			
		}
		else {
			// player can move pieces
			
			// iterate through board pips that the current player owns, 
			//and check what moves are possible with current dice rolls
			int currentPipPosition = 23;
			while(currentPipPosition >= 0){
				
				// Check each individual roll for the pip
				int totalRoll = 0;
				for (int roll_individual : rolls) {
					int positionAreMoveingTo = currentPipPosition - roll_individual;
					int checkIfLegalMove = isLegalMove(currentPipPosition, positionAreMoveingTo);
					if(checkIfLegalMove == 1) {
						// Is legal Move
						int[] legalMove = {currentPipPosition,positionAreMoveingTo,0};
						if(!doesMovePossibilityAlreadyExist(possibleMoves, legalMove)) {
							possibleMoves.add(legalMove);
						}
					}
					else if(checkIfLegalMove == 2) {
						// Is legal enemy take
						int[] legalTake = {currentPipPosition,positionAreMoveingTo,1};
						if(!doesMovePossibilityAlreadyExist(possibleMoves, legalTake)) {
							possibleMoves.add(legalTake);
						}
						// else the move already exists
					}
					totalRoll += roll_individual;
				}
				
				// Check moveing with total of rolls
				int positionAreMoveingTo = currentPipPosition - totalRoll;
				int checkIfLegalMove = isLegalMove(currentPipPosition, positionAreMoveingTo);
				if(checkIfLegalMove == 1) {
					// Legal move
					int[] legalMove = {currentPipPosition,positionAreMoveingTo,0};
					if(!doesMovePossibilityAlreadyExist(possibleMoves, legalMove)) {
						possibleMoves.add(legalMove);
					}
				}
				else if(checkIfLegalMove == 2) {
					// Legal take
					int[] legalTake = {currentPipPosition,positionAreMoveingTo,1};
					if(!doesMovePossibilityAlreadyExist(possibleMoves, legalTake)) {
						possibleMoves.add(legalTake);
					}
				}
				
				
				currentPipPosition--;
			}
		}
		
		
		// return array list containing int[3]
		// int goes-> int[0] is starting position, int[1] is ending position, and [2] is 0 for a move and 1 for a piece take

		if(possibleMoves.isEmpty()) {
			// No moves are possible		(I will make an exception)
			
		}
		
		return possibleMoves;
	}
	
	public boolean doesMovePossibilityAlreadyExist(ArrayList<int[]> listOfPossibleMoves, int[] theMove) {
		if(listOfPossibleMoves.contains(theMove)) {
			return true;
		}
		return false;
	}
}

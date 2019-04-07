import java.util.concurrent.TimeUnit;

/**
 * This is the main class for the Backgammon game. It orchestrates the running of the game.<br>
 * Game Hierarchy in this program : Program [main()] -> Game [startGame()] -> Match [playMatch()] -> Turn [playTurns()]<br>
 * @author Ee En Goh 17202691
 */
public class Backgammon {

    public static final int NUM_PLAYERS = 2;

    // ----- VARIABLES -----
    private final Players players = new Players();
    private final Board board = new Board(players);
    private final UI ui = new UI(board,players);
    public static Backgammon game;
    
    /** Game Point : The maximum point that determines the winner */
    private static int scorePlayingUpTo;
    
    /** Game Score for the current match, which is equivalent to the value of doubling cube too (except value 1) */
    private int scoreForMatch = 1;
    
    /** The player ID of the player who owns the double cube now */
    private static int playerIDWithDoubleCube;
    
    /** The number of game turns in a single match */
    private int matchLength = 0;
    
    // ----- BOOLEAN VARIABLES -----
    /** Boolean value that shows the player instantiation is done : Used for determine case in quit command related method  */
    private static boolean startMatch					= false;
    
    /** Boolean value to check if the doubling play has been enabled */
    private static boolean hasDoublingCubeBeenGiven		= false;
    
    /** Boolean value to check if the exiting of game match is due to rejecting a doubling challenge : quit match but can continue with a new match */
    private static boolean exitingDueToDouble 			= false;
    
    /** Boolean value to check if the exiting of game match is due to rejecting a doubling challenge : quit match but can continue with a new match */
    private static boolean exitingDueToCommand 			= false;
    
    /** Boolean value to check if the game is over which there are winner who has the score higher than the initialized game point : end match while can restart a new game */
    private static boolean gameOver 					= false;
    
    /** Boolean value to check if the players want to go for the next match during the end of match : if not , players can decide whether a new game or quit game */
    private static boolean nextMatch 					= true;
    
    /** Boolean value to check if the players want to start a new game : if not, it will quit the game */
    private static boolean newGame 						= false;
    // ----- END OF BOOLEAN VARIABLES -----
    
    // ----- END OF VARIABLES -----
    
    /**
     * Main flow of the program body
     */
    public static void main(String[] args) throws InterruptedException {
        
    	game = new Backgammon();
        
        do {
        	game.playGame();
        }while(newGame && !exitingDueToCommand);
        
        System.exit(0);
    }
    
    /**
     * Method to setup the game<br>
     * Layer 1
     * @throws InterruptedException
     */
    private void playGame() throws InterruptedException {
    	
    	ui.display();
        ui.displayStartOfGame();
        ui.displayString("");
        
        getEndGamePoint();
        ui.displayString("");
        
        getPlayerNames();
        
        do { 
        	playMatch();
        	debug();
        	
        	/* 
        	 * Ask for the next match only if it is not a quit command given
        	 * or game is not over yet 
        	 */
        	if(!exitingDueToCommand && !gameOver) {
        		nextMatch = nextMatch();
        		if(nextMatch)
        			resetForNewMatch(); 
        	}
        	else { // Else the game over
        		newGame		= false;
        		nextMatch  	= false;
        		
        		if(exitingDueToCommand)
        			quitGameByCommand();
        		
        		winnerScore();
        	}
        	ui.displayString("");
        }while(nextMatch && !exitingDueToCommand);
        
        ui.display_endGame();
        newGame = nextGame();
    }
    
    /**
     * Method to quit game instantly with the command 'quit' with the announcement of winner
     * @throws InterruptedException 
     */
    private void quitGameByCommand() throws InterruptedException {
    	
    	if(exitingDueToCommand) {
    		
    		ui.display_PlayersWantEndGame();
    		winnerScore();
    		ui.display_endGame();
    		System.exit(0);
    		
    	}else {
    		System.out.println("Something wrong here");
    	}
    		
    }
    
    /**
     * Method to get if players wanna go for the next match<br>
     * Layer 1<br>
     * @return	true if yes, else false
     * @throws InterruptedException
     */
    private boolean nextGame() throws InterruptedException {
    	
    	while(true) {
    		
    		ui.promptRestartNewGame();
        	ui.displayString("");
    		
    		try {
        		String reply = ui.getString().toLowerCase().trim();
        		ui.displayString("");
        		
        		if(reply.compareTo("yes") == 0) { 		// Case to play for next game
        			resetForNewGame();
        			return true;
        		}
        		else if(reply.compareTo("no") == 0 || reply.compareTo("quit") == 0) { 	// Case to stop playing
        			newGame = false;
        			return false;
        		}
        		else {
            		ui.displayError_incorrectEntry();
            		continue;
        		}
        		        		
        	}catch (Exception e) {
        		ui.displayError_incorrectEntry();
        		continue;
    		}
    	}
    }
    
    /** 
     * Method that prompt the players to enter the total amount of score to win the whole game<br> 
     * Layer 2<br>
     */
    private int getEndGamePoint() throws InterruptedException {
    	
    	/** If players enter a valid answer */
    	boolean haveGottenAnswer = false;
    	int gamePoint = 0;
    	
    	while(!haveGottenAnswer) {
    		
        	ui.promptPlayersForGamePoint();
        	
        	try {
        		
        		String reply = ui.getString().toLowerCase().trim();
        		
        		if(reply.compareTo("quit") == 0) { // Enable 'quit' in every stage
        			exitingDueToCommand = true;
        			haveGottenAnswer = true;
        			quitGameByCommand();
        			
        		}
        		else	
        			gamePoint = Integer.parseInt(reply);
        		
        		if(gamePoint > 0) {
        			haveGottenAnswer = true;
        			scorePlayingUpTo = gamePoint;
        		}
        		else {
        			ui.displayError_WrongScoreToWinEntered();
        			continue;
        		}
        		
        	}catch (Exception e) {
        		ui.displayError_WrongInputForNumberOfPointsPlayingTo();
        		continue;
    		}
    	}
    	
    	ui.displayString("> Game points playing to: " + gamePoint);
    
    	return gamePoint;
    }
    
    /**
     * Game stage to instantiate player's information (player name here)<br>
     * Layer 2<br>
     */
    private void getPlayerNames() {
    	
        for (Player player : players) {
        	
            ui.promptPlayerName();
            String name = ui.getString().trim();
            
            ui.displayString("> " + name);
            player.setName(name);
            ui.displayPlayerColor(player);
            ui.displayString("");
        }
    }
    
    /**
     * Method to execute the whole game play [ 3 stages ] <br>
     * - Instantiation stage <br>
     * - Game turns stage (repeat until someone wins / quit command) <br>
     * - End Game stage <br>
     * Layer 2<br>
     * @throws InterruptedException
     */
    private void playMatch() throws InterruptedException {
    	
    	startMatch = true;
    	rollToStart();
    	playTurns();
        endCurrentMatch();
    }
    
    /**
     * Boolean method to check if the players want to play a new match with the same match scores<br>
     * Layer 2<br>
     * @return true if yes, else no
     * @throws InterruptedException
     */
    private boolean nextMatch() throws InterruptedException {        
    	
    	while(true) {
        	
    		ui.promptPlayersNextMatch();
    		
        	try {
        		String reply = ui.getString().toLowerCase().trim();
        	
        		if(reply.compareTo("yes") == 0) {		// play again
        			ui.display_PlayersWantNextMatch();
        			nextMatch = true;
        			return true;
        		}
        		else if(reply.compareTo("no") == 0 || reply.compareTo("quit") == 0) { 	// end game
        			ui.display_PlayersWantEndGame();
        			nextMatch = false;
        			newGame = false;
        			return false;
        		}
        		else {
            		ui.displayError_incorrectEntry();
            		continue;
        		}
        		        		
        	}catch (Exception e) {
        		ui.displayError_incorrectEntry();
        		continue;
    		}
    	}
    }
    
    /**
     * Method to reset the game variables to initial states for a new game
     * Layer 2<br>
     * @throws InterruptedException
     */
    private void resetForNewGame() throws InterruptedException  {
    	
    	this.board.resetTheBoard();					// Reset the board
    	this.scoreForMatch = 1;
    	this.matchLength = 0;
    	this.startMatch					= false;
    	this.hasDoublingCubeBeenGiven 	= false;
    	this.exitingDueToDouble 		= false;
    	this.gameOver 					= false;
        this.nextMatch 					= true;
        this.newGame 					= false;
    }
    
    /**
     * Method to reset the game variables to initial states for a new match<br>
     * Thus nextMatch boolean value unchanged<br>
     * Layer 2<br>
     * @throws InterruptedException
     */
    private void resetForNewMatch() throws InterruptedException  {
    	
    	this.board.resetTheBoard();					// Reset the board
    	this.scoreForMatch = 1;
    	this.matchLength = 0;
    	this.startMatch					= false;
    	this.hasDoublingCubeBeenGiven 	= false;
    	this.exitingDueToDouble 		= false;
    	this.gameOver 					= false;
        this.newGame 					= false;
    }
     
    /**
     * Method to determine the game starter, by comparing the single dice roll value from both players<br>
     * Game starter is the player who got the higher dice roll value<br> 
     * Layer 3<br>  
     */
    private void rollToStart() {
    	
        do {

        	// Each player roll dice
        	for (Player player : players) {
                player.getDice().rollDie();
                ui.displayRoll(player);
            }
            
            // Case if both player get the same dice roll value : Roll Again
            if (players.isEqualDice()) { 
                ui.displayDiceEqual();
            }
            
        } while (players.isEqualDice());
        
        players.setCurrentAccordingToDieRoll();     
        ui.displayDiceWinner(players.getCurrent());
        ui.display();
    }
    
    /**
     * Game stage : Each player takes their game turn<br>
     * Layer 3<br>
     * @throws InterruptedException
     */
    private void playTurns() throws InterruptedException {
        
        int currentPlayer_id;
        boolean firstMove = true;      
        
        do {	// Repeat Game Turn
        		
            Player currentPlayer = players.getCurrent();
            ui.print_CurrentPlayer(currentPlayer.toString());
            currentPlayer_id = currentPlayer.getId();
            this.setMatchLength();
            
            // ----- DICE ROLL PART (Including Doubling Dice after first move) -----
            Dice currentDice = diceRollInTurns(firstMove,currentPlayer);
            firstMove = false;
            if(exitingDueToDouble) {
        		return;
        	}
            // ----- END OF DICE ROLL PART -----
            
            // ----- CHECKER MOVE PART -----
            checkerMoveInTurns(currentPlayer, currentDice); 
            ui.display(); // Display information about the player's attempt in current game turn
            
            // ----- END OF CHECKER MOVE PART -----
            
            debug();
            
            // ----- END TURN : switch current player -----
            if(!board.isMatchOver(currentPlayer_id)) {
            	players.advanceCurrentPlayer();
            }/*else {
            	System.out.println("Match is over");
            }*/
            
            ui.display();
            // ----- END OF END TURN -----
            
        } while (!gameOver && !board.isMatchOver(currentPlayer_id) );
    }
    
    /**
     * Game stage : End current match - check if the game is over [ Play until game end ]
     * Layer 3<br>
     * @throws InterruptedException
     */
    private void endCurrentMatch() throws InterruptedException {  
    	
    	// System.out.println("Current match end");
    	startMatch = false;
    	
    	winnerScore();
		ui.display_endMatch(); // Calculate players score, in order to determine if the game over
    	
    	// Check if game over
    	if (board.isMatchOver(players.getCurrent().getId()) || exitingDueToDouble) {
        	        
            int player1_score = players.get(0).getScore();
            int player2_score = players.get(1).getScore();
            
            // Determine of END GAME : A player won if he/she is the first player to have the score that are higher than the play score that up to
            if(player1_score >= scorePlayingUpTo || player2_score >= scorePlayingUpTo) {
            	gameOver = true;
            }                       
        }        					
        TimeUnit.SECONDS.sleep(2); 
    }
    
    /**
     * Method to play the dice roll part in each game turn<br>
     * Layer 4<br>
     * @param firstMove		Boolean value to check if this is the first checker move in this game match
     * @param currentPlayer	Instance of Player class of the current player
     * @return	Dice roll value generated from current game turn
     * @throws InterruptedException
     */
    private Dice diceRollInTurns(boolean firstMove, Player currentPlayer) throws InterruptedException {
    	
    	// Case for first move : The game starter should move the checker with the sum of dice roll value from both players
    	if (firstMove) {
            return new Dice(players.get(0).getDice().getDie(), players.get(1).getDice().getDie());
            
        } else { // Normal game turn
        	
        	// Check if player wants to double
        	
        	// Case : Nobody owns the double cube or current player owns the cube ( been challenged previously ) 
        	if(!hasDoublingCubeBeenGiven || currentPlayer.getId() == playerIDWithDoubleCube) {
        		promptDoubleCubeOption();  // Ask for first double or redouble challenge 
        	}
        	
        	if(!exitingDueToDouble) {
        		currentPlayer.getDice().rollDice();
        		ui.displayRoll(currentPlayer);
        	}
            return currentPlayer.getDice();
        }
    }
    
    /**
     * Method to get a complete move list in each game turn<br>
     * Layer 4<br>
     * @param currentPlayer	
     * @param currentDice
     * @return Given move or cheat command while playing 
     * @throws InterruptedException
     */
    private Command checkerMoveInTurns(Player currentPlayer, Dice currentDice) throws InterruptedException {
    	
    	Command command = new Command();
    	
    	Plays possiblePlays = board.getPossiblePlays(currentPlayer, currentDice);
        
        // Case 1 : No move for current player -> End current game turn
        if (possiblePlays.number() == 0) { 
        	ui.displayNoMove(currentPlayer);
            
        } // Case 2 : Only 1 move available -> forced move for current player
        else if (possiblePlays.number() == 1) { 
        	
            ui.displayForcedMove(currentPlayer);
            board.move(currentPlayer, possiblePlays.get(0));
            
        } // Case 3 : More than 1 move can be made -> Take the move the player chose
        else {  

            ui.displayPlays(currentPlayer, possiblePlays);
            ui.promptCommand(currentPlayer);
            
            command = ui.getCommand(possiblePlays);
            ui.displayString("> " + command);		// Receive and display the given command 
            
            // Case 1 : Current player decides to quit the game 
            if (command.isQuit()) {
            	exitingDueToCommand = true;
            	quitGameByCommand();
            	
            }// Case 2 : Current player is making normal move
            else if (command.isMove()) {
                board.move(currentPlayer, command.getPlay());
                
            } // Case 3 : Current player is making cheat move 
            else if (command.isCheat()) {
                board.cheat();
                
            } // Temporary case to test the ending of game match
            else if (command.isEnd()) { 
            	board.end();
            }  
            
        }
        
        TimeUnit.SECONDS.sleep(2);
        ui.displayString("");
        
        return command;
    }
           
    /**
     * This method will end the game and give points to the relevant player, or lose points
     * @param currentPlayer_id The player that decides to forfeit
     * @throws InterruptedException 
     
    private void endGame(int currentPlayer_id) throws InterruptedException {
    	winnerScore();
    }
    */
    
    /*
    private void continueMatch() throws InterruptedException {
    	
    	this.board.resetTheBoard();	// Reset the board
    	this.scoreForMatch = 1; 		// Reset the score
    	
    	ui.display_CurrentPlayersScores(players.get(0).getScore(), players.get(1).getScore());
    	
    	hasDoublingCubeBeenGiven = false;
    	exitingDueToDouble = false;
    }
    */
    
    /**
     * Method to enable double play on current player if he/she accepts the doubling play<br>
     * Layer 4<br>	
     * @throws InterruptedException
     */
    private void promptDoubleCubeOption() throws InterruptedException {
    	
    	int currentPlayer_id = players.getCurrent().getId();
    	boolean currentPlayerWantsToDouble = askPlayerifWishesToDouble();
    	
    	// Case if current player wants to enable double play
    	if(currentPlayerWantsToDouble) {
    	
    		boolean otherPlayerAcceptsDoubleOffer = opponentAcceptDouble(currentPlayer_id);
    		
    		if(otherPlayerAcceptsDoubleOffer) {		// Case if the opponent accepts the offer
    			hasDoublingCubeBeenGiven = true;
    			ui.print_doubleTheScore();
    		}
    		else {									// Case if the opponent declines the offer - End current game match
    			exitingDueToDouble = true;
    		}
    	}
    }
    

    /**
     * Method to ask and get the response of if the current player want to enable double play before making a valid checker move<br>
     * Layer 5<br>
     * @return True if the current player accepts, else false
     * @throws InterruptedException
     */
    private boolean askPlayerifWishesToDouble() throws InterruptedException {
    
    	if(!hasDoublingCubeBeenGiven)
    		ui.promptPlayerToDouble();
    	else
    		ui.promptPlayerToRedouble();
    	
        String reply = ui.getString();  
        ui.displayString("> " + reply);
        reply.toLowerCase().trim();
        
        if(reply.compareTo("double") == 0) { // The current player wants to enable double play
        	return true;
        }
        else if(reply.compareTo("quit") == 0) {
			exitingDueToCommand = true;
			quitGameByCommand();
			return false;
		}
        else{								 // Else the current player won't	
           	return false;
        }
    }
    
    /**
     * Method to confirm the acceptance of opponent to enable double play<br>
     * Layer 5<br>
     * @param currentPlayer_id	The player ID of the current player
     * @return
     * @throws InterruptedException
     */
    private boolean opponentAcceptDouble(int currentPlayer_id) throws InterruptedException {
    	
    	while(true) {
    		
    		// Confirm the acceptance of double play from opponent
    		ui.displayString("Player " + players.getEnemy().toString() + " , do you want to accept the double? (yes/no)");
        	
    		try {
        		
        		String reply = ui.getString(); 
        		ui.displayString("> " + reply);							// Display user response
        		reply.toLowerCase().trim();
        		
        		if(reply.compareTo("yes") == 0) {

        			scoreForMatch *=  2;        						// Double the score
        			
        			if(currentPlayer_id == 0) {        					// The double dice is now owned by the opponent
        				playerIDWithDoubleCube = 1;      				// Opponent has ID number 2
        			}else {
        				playerIDWithDoubleCube = 0;      				// Opponent has ID number 1
        			}
        			return true;
        		}
        		else if(reply.compareTo("no") == 0) {
        			return false;
        		}
        		else if(reply.compareTo("quit") == 0) {
        			exitingDueToCommand = true;
        			quitGameByCommand();
        		}
        		else {
            		ui.displayError_incorrectEntry();
            		continue;
        		}
        		        		
        	}catch (Exception e) {
        		ui.displayError_incorrectEntry();
        		continue;
    		}
    	}
    }
    
    /**
     * Method to calculate the match score earned in recent match, announce winner and sum of points earned so far<br>
     * **Current implementation expecting current player is the winner and opponent is the loser<br>
     * @throws InterruptedException 
     */
    private void winnerScore() throws InterruptedException {
    	
    	int winner_curr_score = players.getCurrent().getScore();
    	int opponent_ID = players.getEnemy().getId();
    	
    	// END GAME 
    	// Case if the current player uses the 'quit' command 
    	if(exitingDueToCommand) {
    		if(startMatch)
    			ui.displayString(players.getCurrent().toString() + " declare forfeit, thus " + players.getEnemy().toString() + " WIN THE GAME !!");
    		
    	} // Case if the players decide not to continue the game matches : Compare their score and winner is the one with higher score
    	else if(!nextMatch) { // TODO
			
    		if(players.getCurrent().getScore() > players.getEnemy().getScore())
				ui.display_gameWinner(players.getCurrent().toString());
			
			else if(players.getCurrent().getScore() < players.getEnemy().getScore())
				ui.display_gameWinner(players.getEnemy().toString());
			
			else 
				ui.displayString("IT IS A DEUCE !!"); // Only happen when quit game suddenly or stop to continue the next match	
		}
    	  // END MATCH 
    	  // Case if a match end by rejecting double play
    	else if(exitingDueToDouble) { 
			players.getCurrent().setScore(winner_curr_score + scoreForMatch);
    		ui.print_rejectedDoubleTheScore(players.getCurrent().toString(), players.getEnemy().toString(), scoreForMatch); 
    		
    	} // Case if a match end normally 	 
    	else if(board.isMatchOver(players.getCurrent().getId())) {    	
    		
    		// ----- CALCULATE SCORE -----
    		// Case if the loser has at least 1 checker in borne off : Winner gets single game score [1 unit point]
    		if(board.isCheckersBorneOff(opponent_ID)) {
    			players.getCurrent().setScore(winner_curr_score + scoreForMatch);
    			ui.display_roundWinner(players.getCurrent());
    			
    		}	// Else no checker borne off
    		else{ 
    		
    			// Case if Backgammoned : Winner gets backgammoned score [3 unit points]
    			if(board.isCheckersInBar(opponent_ID) || board.isCheckersInOpponentHome(opponent_ID)) {
    				players.getCurrent().setScore(winner_curr_score + scoreForMatch * 3);
    			
    			} // Case if Gammoned : Winner gets gammoned score [2 unit points]
    			else{
    				players.getCurrent().setScore(winner_curr_score + scoreForMatch * 2);
    			}
    			
    			ui.display_roundWinner(players.getCurrent());
    		}
    		
    		// ----- END OF CALCULATE SCORE -----
    	}
    	
    	// Announce the score of both player    	
        ui.display_CurrentPlayersScores(players);
    }
    
    /**
     * @return The number of game turns in the current match
     */
    public int getMatchLength() {
    	return this.matchLength;
    }
    
    /**
     * Method to increase the match length when each game turn starts in a match
     */
    private void setMatchLength() {
    	this.matchLength++;
    }
    
    // This method is to remove code duplication. I will finish it later and it will return a boolean no void
    private void yesOrNoResponseHelper() {
    	
    }
    
    /**
     * Method for efficient debugging
     */
    private void debug() {
    	
    	System.out.println("[ In current game turn ]");
    	System.out.println("Current Player\t\t: " + players.getCurrent().toString());
    	System.out.println("Match Length\t\t: " + getMatchLength());
    	System.out.println("Match Score\t\t: C - " + players.getCurrent().getScore() + " ; E - " + players.getEnemy().getScore());
    	System.out.println("Game Point\t\t: " + scorePlayingUpTo);
    	System.out.println("Doubling Dice\t\t: " + scoreForMatch);
    	System.out.println("Player has doubing cube\t: " + players.get(playerIDWithDoubleCube).toString());
    	System.out.println("Start Match\t\t: " + startMatch);
    	System.out.println("Doubling Cube Given\t: " + hasDoublingCubeBeenGiven);
    	System.out.println("Exiting for command \t: " + exitingDueToCommand);
    	System.out.println("Exiting for reject DP\t: " + exitingDueToDouble);
    	System.out.println("Game Over\t\t: " + gameOver);
    	System.out.println("Play next MATCH\t\t: " + nextMatch);
    	System.out.println("Play new GAME\t\t: " + newGame);
    	System.out.println("");
    }
}

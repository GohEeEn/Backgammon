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
    private int scoreForGame = 1;
    
    /** The player ID of the player who owns the double cube now */
    private static int currentPlayerWhoHasDoubleCube;
    
    /** The number of game turns in a single match */
    private int matchLength = 0;
    
    private static boolean hasDoubleingCubeBeenGiven	= false;
    private static boolean exitingDueToDouble 			= false;
    private static boolean exitingDueToCommand 			= false;
    private static boolean gameOver 					= false;
    private static boolean nextMatch 					= false;
    private static boolean newGame 						= false;
    
    // ----- END OF VARIABLES -----
    
    /**
     * Main flow of the program body
     */
    public static void main(String[] args) throws InterruptedException {
        
    	game = new Backgammon();
        
        do {
        	game.playGame();
        }while(newGame);
        
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
        	nextMatch = nextMatch();
        	reset();
        	ui.displayString("");
        }while(nextMatch && !gameOver);
        
        newGame = nextGame();
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
        	
    		try {
        		String reply = ui.getString().toLowerCase().trim();
        		
        		if(reply.compareTo("yes") == 0) { 		// Case to play for next game
        			reset();
        			return true;
        		}
        		else if(reply.compareTo("no") == 0) { 	// Case to stop playing
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
        		
        		gamePoint = Integer.parseInt(ui.getString().trim());
        		
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
        	
    		ui.promptPlayersIfWantToPlayAgain();
    		
        	try {
        		String reply = ui.getString().toLowerCase().trim();
        	
        		if(reply.compareTo("yes") == 0) {		// play again
        			ui.display_PlayersWantNextMatch();
        			return true;
        		}
        		else if(reply.compareTo("no") == 0) { 	// end game
        			ui.display_PlayersWantEndGame();
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
     * Method to reset the game variables to initial states
     * Layer 2<br>
     * TODO
     * @throws InterruptedException
     */
    private void reset() throws InterruptedException  {
    	
    	this.board.resetTheBoard();					// Reset the board
    	this.scoreForGame = 1;
    	this.matchLength = 0;
    	this.hasDoubleingCubeBeenGiven 	= false;
    	this.exitingDueToDouble 		= false;
    	this.gameOver 					= false;
        this.nextMatch 					= false;
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

            // ----- END TURN : switch current player ----- 
            players.advanceCurrentPlayer(); // TODO
            ui.display();
            // ----- END OF END TURN -----
            
        } while (!exitingDueToCommand && !gameOver && !board.isMatchOver(currentPlayer_id) );
    }
    
    /**
     * Game stage : End current match - check if the game is over
     * Layer 3<br>
     * TODO
     * @throws InterruptedException
     */
    private void endCurrentMatch() throws InterruptedException { // endGame Implementation needed 
    	
    	if (board.isMatchOver(players.getCurrent().getId()) || exitingDueToDouble) {
        	
        	// Normal Winning Stage
        	if(!exitingDueToDouble) {
                ui.displayGameWinner(board.getWinner());                
                winnerScore();
                
        	}else;
        	
            TimeUnit.SECONDS.sleep(2); 
            
            int player1_score = players.get(0).getScore();
            int player2_score = players.get(1).getScore();
            
            System.out.println(" player 1 [" + players.get(0).toString() + "] currently has score " + player1_score);
            System.out.println(" player 2 [" + players.get(1).toString() + "] currently has score " + player2_score);
            
            // Determine of END GAME : A player won if he/she is the first player to have the score that are higher than the play score that up to
            if(player1_score >= scorePlayingUpTo || player2_score >= scorePlayingUpTo) {
            	
            	if(player1_score >= scorePlayingUpTo) {
            		ui.display_gameWinner(players.getCurrent().toString());
            	}
            	else {
            		ui.display_gameWinner(players.getCurrent().toString());
            	}
            	gameOver = true;
            }
        }
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
        	if(!hasDoubleingCubeBeenGiven || currentPlayer.getId() == currentPlayerWhoHasDoubleCube) {
        		promptDoubleCubeOption();  // Ask for first double or redouble challenge TODO
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
     */
    private void endGame(int currentPlayer_id) {
    	winnerScore();
    }
    
    private void continueMatch() throws InterruptedException {
    	
    	this.board.resetTheBoard();	// Reset the board
    	this.scoreForGame = 1; 		// Reset the score
    	
    	ui.display_CurrentPlayersScores(players.get(0).getScore(), players.get(1).getScore());
    	
    	hasDoubleingCubeBeenGiven = false;
    	exitingDueToDouble = false;
    }
    
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
    			hasDoubleingCubeBeenGiven = true;
    			ui.print_doubleTheScore();
    		}
    		else {									// Case if the opponent declines the offer - End current game match
    			
    			ui.display_endingGame();			// End Game
    			exitingDueToDouble = true;
    			winnerScore();
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
    
    	ui.promptPlayerToDouble();
        String reply = ui.getString().toLowerCase().trim();
        
        if(reply.compareTo("double") == 0) { // The current player wants to enable double play
        	return true;
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
    		
    		ui.displayString(players.getEnemy().toString());	// Notify the opponent
        	ui.promptOpponentIfAcceptDouble(); 					// Confirm the acceptance of double play from opponent
        	
        	try {
        		
        		String reply = ui.getString().toLowerCase().trim();
        		
        		if(reply.compareTo("yes") == 0) {

        			scoreForGame = scoreForGame * 2;        			// Double the score
        			
        			if(currentPlayer_id == 0) {        					// The double dice is now owned by the opponent
        				currentPlayerWhoHasDoubleCube = 1;      		// Opponent has ID number 2
        			}else {
        				currentPlayerWhoHasDoubleCube = 0;      		// Opponent has ID number 1
        			}
        			return true;
        		}
        		else if(reply.compareTo("no") == 0) {
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
     * Method to calculate the match score and increment with points that the round winner earns in current round<br>
     * **Current implementation expecting current player is the winner and opponent is the loser<br>
     */
    private void winnerScore() {
    	
    	int winner_curr_score = players.getCurrent().getScore();
    	int opponent_ID = players.getEnemy().getId();
    	
    	// END GAME 
    	// Case if the players decide not to continue the game matches or using quit command
    	if(!nextMatch){
    		
    		if(players.getCurrent().getScore() > players.getEnemy().getScore())
    			ui.display_gameWinner(players.getCurrent().toString());
    		else if(players.getCurrent().getScore() < players.getEnemy().getScore())
    			ui.display_gameWinner(players.getEnemy().toString());
    		else 
    			ui.displayString("IT IS A DEDUCE GAME !!"); // Only happen when quit game suddenly or stop to continue the next match
    		
    	} // Case if the current player uses the 'quit' command
    	else if(exitingDueToCommand) {
    		
    	} // END MATCH 
    	  // Case if the game end by rejecting double play
    	else if(exitingDueToDouble) { 
			players.getCurrent().setScore(winner_curr_score + scoreForGame);
    		ui.print_rejectedDoubleTheScore(players.getCurrent().toString(),players.getEnemy().toString(), winner_curr_score + scoreForGame); 
    		
    	}	// Case if the loser has at least 1 checker in borne off : Winner gets single game score [1 unit point] 
    	else if(board.isCheckersBorneOff(opponent_ID)) {
    		players.getCurrent().setScore(winner_curr_score + scoreForGame);
    		ui.display_roundWinner(players.getCurrent());
    		
    	}	// Else no checker borne off
    	else{ 
    		
    		// Case if Gammoned : Winner gets gammoned score [2 unit points]
    		if(board.isCheckersInBar(opponent_ID) && board.isCheckersInOpponentHome(opponent_ID)) {
    			players.getCurrent().setScore(winner_curr_score + scoreForGame * 2);
    			
    		} // Case if Backgammoned : Winner gets backgammoned score [3 unit points]
    		else{
    			players.getCurrent().setScore(winner_curr_score + scoreForGame * 3);
    		}
    		
    		ui.display_roundWinner(players.getCurrent());
    	}
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
    
    
}

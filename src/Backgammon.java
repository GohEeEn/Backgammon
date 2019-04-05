import java.util.concurrent.TimeUnit;

/**
 * This is the main class for the Backgammon game. It orchestrates the running of the game.
 * @author Ee En Goh 17202691
 */
public class Backgammon {

    public static final int NUM_PLAYERS = 2;

    private final Players players = new Players();
    private final Board board = new Board(players);
    private final UI ui = new UI(board,players);
    
    private static int player1_score = 0;
    private static int player2_score = 0;
    
    private int scoreForGame = 1;
    private static int scorePlayingUpTo;
    
    public static Backgammon game;
    
    private static boolean hasDoubleingCubeBeenGiven = false;
    private static int currentPlayerWhoHasDoubleCube;
    
    private static boolean exitingDueToDouble = false;
    
    /**
     * Game stage to instantiate player's information (player name here)
     */
    private void getPlayerNames() {
        for (Player player : players) {
        	
            ui.promptPlayerName();
            String name = ui.getString();
            
            ui.displayString("> " + name);
            player.setName(name);
            ui.displayPlayerColor(player);
        }
    }
    
    /** 
     * The amount of individual games player must win to win the game 
     */
    private int getHowManyGamesPlayerWantsToPlayUpTo() throws InterruptedException {
    	
    	// Prompt the players to enter how many points they are playing up to
    	boolean haveGottenAnswer = false;
    	
    	int numberOfPointsPlayingTo = 0;
    	
    	while(!haveGottenAnswer) {
        	ui.promptPlayersForNumberOfPointsWantToPlayTo();
        	try {
        		numberOfPointsPlayingTo = Integer.parseInt(ui.getString());
        		if(numberOfPointsPlayingTo > 0) {
        			haveGottenAnswer = true;
        			scorePlayingUpTo = numberOfPointsPlayingTo;
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
    	
    	ui.displayString("> number of points playing to: " + numberOfPointsPlayingTo);
    
    	return numberOfPointsPlayingTo;
    }
    
    private boolean getIfPlayersWantToPlayAgain() throws InterruptedException {
    	
    	Command command = new Command();
    	boolean haveGottenAnswer = false;
    	boolean playersWantToPlayAgain = false;
    	
    	while(!haveGottenAnswer) {
        	ui.promptPlayersIfWantToPlayAgain();
        	try {
        		String theAnswerGivenByPlayers = ui.getString();
        		if(theAnswerGivenByPlayers.compareTo("yes") == 0) {
        			// Repeat game
        			return true;
        		}
        		else if(theAnswerGivenByPlayers.compareTo("no") == 0) {
        			// end game
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
    	
    	return playersWantToPlayAgain;
    }
    
    private boolean getIfPlayersWantToPlayNextGame() throws InterruptedException {
    	Command command = new Command();
    	boolean haveGottenAnswer = false;
    	
    	boolean playersWantToPlayNextGame = false;
    	
    	while(!haveGottenAnswer) {
        	ui.promptPlayersIfWantToPlayNextGame();
        	try {
        		String theAnswerGivenByPlayers = ui.getString();
        		if(theAnswerGivenByPlayers.compareTo("yes") == 0) {
        			// Repeat game
        			return true;
        		}
        		else if(theAnswerGivenByPlayers.compareTo("no") == 0) {
        			// end game
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
    	
    	return playersWantToPlayNextGame;
    }
    
    /**
     * Game Stage : Each player rolls a single dice to decide the game starter  
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
     * Game stage : Each player takes their game turn
     * @throws InterruptedException
     */
    private void takeTurns() throws InterruptedException {
        
    	Command command = new Command();
        boolean firstMove = true;
        int currentPlayer_id;
        
        do {
        		
            Player currentPlayer = players.getCurrent();
            ui.print_CurrentPlayer(currentPlayer.toString());
            currentPlayer_id = currentPlayer.getId();
            
            Dice currentDice;
            
            // ----- Dice Roll Part -----
            
            // Case for first move : The game starter should move the checker with the sum of dice roll value from both player
            if (firstMove) {
                currentDice = new Dice(players.get(0).getDice().getDie(),players.get(1).getDice().getDie());
                firstMove = false;
                
            } else { // Normal game turn
            	
            	// Check if player wants to double
            	
            	// Case 1 : Nobody owns the double cube
            	if(!hasDoubleingCubeBeenGiven) {
            		promptDoubleCubeOption(currentPlayer_id);  
            	}
            	else {
            		
            		// Case else : current player has the cube
            		if(currentPlayer_id == currentPlayerWhoHasDoubleCube) {
            			promptDoubleCubeOption(currentPlayer_id); // then: current player has the double
            		}
            	}
            	
            	if(exitingDueToDouble) {
            		return;
            	}
            	
                currentPlayer.getDice().rollDice();
                ui.displayRoll(currentPlayer);
                currentDice = currentPlayer.getDice();
            }
            
            // ----- End of Dice Roll Part -----
            
            // ----- Game Play Part -----
            Plays possiblePlays;
            possiblePlays = board.getPossiblePlays(currentPlayer,currentDice);
            
            // Case 1 : No move for current player -> End current game turn
            if (possiblePlays.number() == 0) { 
            	ui.displayNoMove(currentPlayer);
                
            } else if (possiblePlays.number() == 1) { // Case 2 : Only 1 move available -> forced move for current player
            	
                ui.displayForcedMove(currentPlayer);
                board.move(currentPlayer, possiblePlays.get(0));
                
            } else {  // Case 3 : More than 1 move can be made -> Take the move the player chose

                ui.displayPlays(currentPlayer, possiblePlays);
                ui.promptCommand(currentPlayer);
                
                command = ui.getCommand(possiblePlays);
                ui.displayString("> " + command);
                
                // Case 1 : Current player is making normal move
                if (command.isMove()) {
                    board.move(currentPlayer, command.getPlay());
                    
                    /*
                    if(board.checkIfCurrentPlayerHasWon(currentPlayer.getId())) {
                    	board.
                    }
                    */
                } // Case 2 : Current player is making cheat move 
                else if (command.isCheat()) {
                    board.cheat();
                }
            }
            
            ui.display(); // Display information about the player's attempt in current game turn
            
            // ----- End of Game Play Part -----
            
            TimeUnit.SECONDS.sleep(2);
            
            // Switch Player : End current game turn
            players.advanceCurrentPlayer();
            ui.display();
            
        } while (!command.isQuit() && !board.isGameOver(currentPlayer_id) && !exitingDueToDouble);
    }

    private void startGame() throws InterruptedException {
//        board.setUI(ui
    	// set up the game
    	player1_score = 0;
    	player2_score = 0;
        ui.display();
        ui.displayStartOfGame();
        getHowManyGamesPlayerWantsToPlayUpTo();
        getPlayerNames();
        
        
        this.play();
        /*
        takeTurns();
        if (board.isGameOver()) {
            ui.displayGameWinner(board.getWinner());
            TimeUnit.SECONDS.sleep(2);
            
            boolean playerWantsToPlayAgain = getIfPlayersWantToPlayAgain();
            
            if(playerWantsToPlayAgain == true) {
            	// Players want to repeat
            	ui.display_PlayersWantToRepeatGame();
            	this.playAgain();

            }else {
            	// end game
            	ui.display_PlayersWantToEndGame();
            }
        }
        */
        
    }
    
    /**
     * Method to execute the whole game play [ 3 stages ] <br>
     * - Instantiation stage [ rollToStart() ]<br>
     * - Game turns stage [ takeTurns() ]<br>
     * - End Game stage
     * @throws InterruptedException
     */
    private void play() throws InterruptedException {
    	
    	rollToStart();
        takeTurns();
        
        // ----- END GAME STAGE -----
        
        if (board.isGameOver(players.getCurrent().getId()) || exitingDueToDouble) {
        	
        	// Normal Winning Stage
        	if(!exitingDueToDouble) {
                ui.displayGameWinner(board.getWinner());                
                endGame(players.getCurrent().getId());	
        	}else;
            
            TimeUnit.SECONDS.sleep(2);
            
            System.out.println(" player 1s score is " + player1_score);
            System.out.println(" player 2s score is " + player2_score);
            
            // A player won if he/she is the first player to have the score that are higher than the play score that up to
            if(player1_score >= scorePlayingUpTo || player2_score >= scorePlayingUpTo) {
            	
            	if(player1_score >= scoreForGame) {
            		ui.print_endFullGameMessage(1, players.getCurrent().toString());
            	}
            	else {
            		ui.print_endFullGameMessage(2, players.getCurrent().toString());
            	}
            } 
            else { // Game should continue as nobody has won yet
            	
                boolean playerWantsToPlayNextGame = this.getIfPlayersWantToPlayNextGame();
                
                if(playerWantsToPlayNextGame == true) { // Players want to continue
                	ui.display_PlayersWantNextGame();
                	this.playAgain();

                }else { // End game
                	ui.display_PlayersWantToEndGame();
                }
            }
        }
    }
    
    /**
     * This method will end the game and give points to the relevant player, or lose points
     * @param currentPlayer_id The player that decides to forfeit
     */
    private void endGame(int currentPlayer_id) {
    	
    	// The opponent is player 2 [player 1 has player ID 0]
		if(currentPlayer_id == 0) { 
			player2_score += scoreForGame;
		}
		else { // Else the opponent is player 1
			player1_score += scoreForGame; 
		}
    	
    }
    
    private void playAgain() throws InterruptedException {
    	
    	this.board.resetTheBoard();	// Reset the board
    	this.scoreForGame = 1; 		// Reset the score
    	
    	ui.display_CurrentPlayersScores(player1_score, player2_score);
    	
    	hasDoubleingCubeBeenGiven = false;
    	exitingDueToDouble = false;
    	this.play();
    }
    
    private void restartGame() throws InterruptedException  {
    	hasDoubleingCubeBeenGiven = false;
    	
    	this.board.resetTheBoard();	// Reset the board
    	this.scoreForGame = 1; 		// Reset the score
    	
    	startGame();
    }
    
    
    private void promptDoubleCubeOption(int currentPlayer_id) throws InterruptedException {
    	
    	boolean currentPlayerWantsToDouble = askPlayerifWishesToDouble();
    	
    	if(currentPlayerWantsToDouble) {
    	
    		boolean otherPlayerAcceptsDoubleOffer = askPlayerIfAcceptsDoubleOffer(currentPlayer_id);
    		if(otherPlayerAcceptsDoubleOffer) {
    			hasDoubleingCubeBeenGiven = true;
    			ui.print_doubleTheScore();
    		}
    		else {
    			
    			ui.display_endingGame();

    			if(currentPlayer_id == 0) {
    				// the other play is 1
    				player1_score = player1_score + scoreForGame;
    				ui.print_rejectedDoubleTheScore(players.get(0).toString(),players.get(1).toString(), player1_score, scoreForGame);
    			}
    			else {
    				// the other player is 2
    				player2_score = player2_score + scoreForGame;
    				ui.print_rejectedDoubleTheScore(players.get(1).toString(),players.get(0).toString(), player2_score, scoreForGame);
    			}
    			
    			exitingDueToDouble = true;
    			
    			
    		}
    	}
    }
    
    private boolean askPlayerifWishesToDouble() throws InterruptedException {
    	
    	Command command = new Command();
    	
    	boolean haveGottenAnswer = false;
    	
    	boolean playersWantToPlayAgain = false;
    	
    	while(!haveGottenAnswer) {
        	ui.promptPlayerIfWantToDouble();
        	try {
        		String theAnswerGivenByPlayers = ui.getString();
        		if(theAnswerGivenByPlayers.compareTo("yes") == 0) {
        			return true;
        		}
        		else if(theAnswerGivenByPlayers.compareTo("no") == 0) {
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
    	
    	return playersWantToPlayAgain;
    }
    
    private boolean askPlayerIfAcceptsDoubleOffer(int currentPlayer_id) throws InterruptedException {
    	Command command = new Command();
    	
    	boolean haveGottenAnswer = false;
    	
    	boolean playersWantToPlayAgain = false;
    	
    	while(!haveGottenAnswer) {
        	ui.promptPlayerifWantToAcceptDouble();
        	try {
        		String theAnswerGivenByPlayers = ui.getString();
        		if(theAnswerGivenByPlayers.compareTo("yes") == 0) {
        			// Double the score
        			scoreForGame = scoreForGame * 2;
        			// Give the dice to the other player
        			if(currentPlayer_id == 0) {
        				// enemy player is 2
        				currentPlayerWhoHasDoubleCube = 1;
        			}else {
        				// enemey player is 1
        				currentPlayerWhoHasDoubleCube = 0;
        			}
        			
        			return true;
        		}
        		else if(theAnswerGivenByPlayers.compareTo("no") == 0) {
        			
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
    	
    	return playersWantToPlayAgain;
    }
    
    // This method is to remove code duplication. I will finish it later and it will return a boolean no void
    private void yesOrNoResponseHelper() {
    	
    }

    public static void main(String[] args) throws InterruptedException {
        game = new Backgammon();
        game.startGame();
        System.exit(0);
    }
}

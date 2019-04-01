import java.util.concurrent.TimeUnit;

public class Backgammon {
    // This is the main class for the Backgammon game. It orchestrates the running of the game.

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
    
    private void getPlayerNames() {
        for (Player player : players) {
            ui.promptPlayerName();
            String name = ui.getString();
            ui.displayString("> " + name);
            player.setName(name);
            ui.displayPlayerColor(player);
        }
    }
    
    // The amount of individual games player must win to win the game
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
    
    

    private void rollToStart() {
        do {
            for (Player player : players) {
                player.getDice().rollDie();
                ui.displayRoll(player);
            }
            if (players.isEqualDice()) {
                ui.displayDiceEqual();
            }
        } while (players.isEqualDice());
        players.setCurrentAccordingToDieRoll();
        ui.displayDiceWinner(players.getCurrent());
        ui.display();
    }

    private void takeTurns() throws InterruptedException {
        Command command = new Command();
        boolean firstMove = true;
        int currentPlayer_id;
        do {
            Player currentPlayer = players.getCurrent();
            ui.print_CurrentPlayer(currentPlayer.toString());
            currentPlayer_id = currentPlayer.getId();
            Dice currentDice;
            if (firstMove) {
                currentDice = new Dice(players.get(0).getDice().getDie(),players.get(1).getDice().getDie());
                firstMove = false;
            } else {
            	// Check if player wants to double
            	if(!hasDoubleingCubeBeenGiven) {
            		// then: Nobody owns the double cube
            			promptDoubleCubeOption(currentPlayer_id);
            	}
            	else {
            		// Then: one of the players has the cube
            		if(currentPlayer_id == currentPlayerWhoHasDoubleCube) {
            			// then: current player has the double
            			promptDoubleCubeOption(currentPlayer_id);
            		}
            	}
            	
            	if(exitingDueToDouble) {
            		return;
            	}
                currentPlayer.getDice().rollDice();
                ui.displayRoll(currentPlayer);
                currentDice = currentPlayer.getDice();
            }
            Plays possiblePlays;
            possiblePlays = board.getPossiblePlays(currentPlayer,currentDice);
            if (possiblePlays.number()==0) {
            	// no move for current player
                ui.displayNoMove(currentPlayer);
            } else if (possiblePlays.number()==1) {
            	// forced move for current player
                ui.displayForcedMove(currentPlayer);
                board.move(currentPlayer, possiblePlays.get(0));
            } else {
            	// Take the move the player chose
                ui.displayPlays(currentPlayer, possiblePlays);
                ui.promptCommand(currentPlayer);
                command = ui.getCommand(possiblePlays);
                ui.displayString("> " + command);
                if (command.isMove()) {
                    board.move(currentPlayer, command.getPlay());
                    
                    /*
                    if(board.checkIfCurrentPlayerHasWon(currentPlayer.getId())) {
                    	board.
                    }
                    */
                } else if (command.isCheat()) {
                    board.cheat();
                }
            }
            ui.display();
            TimeUnit.SECONDS.sleep(2);
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
    
    private void play() throws InterruptedException {
    	// Play game
    	rollToStart();
        takeTurns();
        /*
        if(exitingDueToDouble) {
        	TimeUnit.SECONDS.sleep(2);
        	
        	
        }
        */
        if (board.isGameOver(players.getCurrent().getId()) || exitingDueToDouble) {
        	if(!exitingDueToDouble) {
                ui.displayGameWinner(board.getWinner());
                
                endGame(players.getCurrent().getId());	
        	}else;
            
            TimeUnit.SECONDS.sleep(2);
            
            System.out.println(" player 1s score is " + player1_score);
            System.out.println(" player 2s score is " + player2_score);
            
            if(player1_score >= scorePlayingUpTo || player2_score >= scorePlayingUpTo) {
            	// Then a player has won
            	if(player1_score >= scoreForGame) {
            		// player 1 has won
            		ui.print_endFullGameMessage(1, players.getCurrent().toString());
            	}
            	else {
            		// player 2 has won
            		ui.print_endFullGameMessage(2, players.getCurrent().toString());
            	}
            } 
            else {
            	// Game should continue as nobody has one yet
                boolean playerWantsToPlayNextGame = this.getIfPlayersWantToPlayNextGame();
                
                if(playerWantsToPlayNextGame == true) {
                	// Players want to continue
                	ui.display_PlayersWantNextGame();
                	this.playAgain();

                }else {
                	// end game
                	ui.display_PlayersWantToEndGame();
                }
            }
        }
    }
    
    // This method will end the game and give points to the relevant player, or lose points
    private void endGame(int currentPlayer_id) {
    	
		if(currentPlayer_id == 0) {
			// the other play is 2
			player2_score += scoreForGame;
		}
		else {
			// the other player is 1
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
    
    // This method is to remove code duplication. I will finnish it later and it will return a boolean no void
    private void yesOrNoResponseHelper() {
    	
    }

    public static void main(String[] args) throws InterruptedException {
        game = new Backgammon();
        game.startGame();
        System.exit(0);
    }
    
    
}

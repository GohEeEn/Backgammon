import java.util.concurrent.TimeUnit;

/**
 * This is the main class for the Backgammon game. It orchestrates the running of the game.<br>
 * Game Hierarchy in this program : Program [main()] -> Match [playMatch()] -> Game [playGame()] -> Turn [playTurns()]<br>
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class Backgammon {

    public static final int NUM_PLAYERS = 2;
    private static final String[] BOT_NAMES = {"Francis"};

    // ----- VARIABLES -----
    private final Players players = new Players();
    private final Board board = new Board(players);
    private final Cube cube = new Cube();
    private final Game realGame = new Game(board, cube, players);
    private final Match match = new Match(realGame, cube, players);
    private BotAPI[] bots = new BotAPI[NUM_PLAYERS];
    private final UI ui = new UI(board,players,cube,match,bots);
    private String[] botNames = new String[NUM_PLAYERS];
    
    // ----- BOOLEAN VARIABLES -----
    /** Boolean value that shows the player instantiation is done : Used for determine case in quit command related method  */
    private static boolean startGame					= false;
    
    /** Boolean value to check if the players want to go for the next game during the end of current game : if not , players can decide whether a new match or quit */
    private static boolean nextGame 					= true;
    
    /** Boolean value to check if the players want to start a new match : if not, it will quit the program */
    private static boolean newMatch 					= false;
    
    // ----- END OF BOOLEAN VARIABLES -----
    
    // ----- END OF VARIABLES -----
    
    Backgammon() throws InterruptedException{
    	
    	ui.display();
        ui.displayStartOfGame();
        ui.displayString("");
        
    	do {
        	playMatch();
        }while(newMatch);
    }
    
    /**
     * Main flow of the program body
     */
    public static void main(String[] args) throws InterruptedException {
        
    	Backgammon game = new Backgammon();
        System.exit(0);
    }
    
    
    
    /**
     * Method to setup a match <br>
     * Layer 1
     * @throws InterruptedException
     */
    private void playMatch() throws InterruptedException {
    	
    	
        
        // Set the match point of the current match
        match.setMatchPoint(getMatchPoint());
        ui.displayString("");
        
        //getPlayerNames();        // Instantiate players name
        getPlayerName_botTest();
        
        do { 
        	playGame();			// Play a game
        	debug();
        	
        	/* 
        	 * Ask for the next match only if it is not a quit command given
        	 * or match is not over yet  
        	 */
        	if(!realGame.getResignedByCommand() && !match.isOver()) {
        		nextGame = nextGame();
        		match.setLength();
        		ui.display();
        		
        	} // Else the match over (By command or full match is end)
        	else { 
        		newMatch	= false;
        		nextGame  	= false;
        		
        		if(realGame.getResignedByCommand())
        			quitGameByCommand();
        		
        	}
        	ui.display();
        	ui.displayString("");
        	
        }while(nextGame && !realGame.getResignedByCommand());
        
        displayEndStage();
        ui.display_endMatch();
        newMatch = nextMatch();
    }
    
    /**
     * Method to quit game instantly with the command 'quit' with the announcement of winner
     * @throws InterruptedException 
     */
    private void quitGameByCommand() throws InterruptedException {
    		
    	if(match.isOver()) {
    		ui.display_PlayersWantQuit();
    		displayEndStage();
    		ui.display_endProgram(); 
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
    private boolean nextMatch() throws InterruptedException {
    	
    	while(true) {
    		
    		ui.promptRestartNewMatch();
        	ui.displayString("");
    		
    		try {
        		String reply = ui.getString().toLowerCase().trim();
        		ui.displayString("");
        		
        		// Case to play for next game
        		if(reply.compareTo("yes") == 0) {
        			match.setLength();
        			ui.display_PlayersWantNextMatch();
        			resetMatch();
        			ui.display_newMatch();
        			return true;
        			
        		} // Case to stop playing
        		else if(reply.compareTo("no") == 0 || reply.compareTo("quit") == 0) {
        			displayEndStage();
        			return false;
        		}
        		else
            		ui.displayError_incorrectEntry();
        		
        	}catch (Exception e) { 
        		ui.displayError_incorrectEntry();
        	}
    	}
    }
    
    /** 
     * Method that prompt the players to enter the total amount of score to win the whole game<br> 
     * Layer 2<br>
     */
    private int getMatchPoint() throws InterruptedException {
    	
    	/** If players enter a valid answer */
    	boolean haveGottenAnswer = false;
    	int matchPoint = 0;
    	
    	while(!haveGottenAnswer) {
    		
        	ui.promptPlayersForGamePoint();
        	
        	try {
        		
        		String reply = ui.getString().toLowerCase().trim();
        		
        		if(reply.compareTo("quit") == 0) { // Enable 'quit' in every stage
        			haveGottenAnswer = true;
        			realGame.setResignedByCommand(players.getCurrent());
        			quitGameByCommand();
        			
        		}
        		else	
        			matchPoint = Integer.parseInt(reply);
        		
        		if(matchPoint > 0) {	// Set match point if a valid value given
        			haveGottenAnswer = true;
        			match.setMatchPoint(matchPoint);
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
    	
    	ui.displayString("> Game points playing to: " + matchPoint);
    
    	return matchPoint;
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
    
    // For bot testing and training
    private void getPlayerName_botTest() {
    	// Set up francis with player 1
        players.get(0).setName(Francis.getTheName());
    	ui.displayString("> Francis bot controlls player 1");
    	ui.displayPlayerColor(players.get(0));
    	
    	// set up bot1 with player 2
        players.get(1).setName(Bot1.getTheName());
    	ui.displayString("> Bot1 bot controlls player 2");
    	ui.displayPlayerColor(players.get(1));
    	
    	// Set up the francis bot
    	bots[0] = new Francis(players.get(0),players.get(1),board,cube,match,ui.getInfoPanel());    	
    	
    	// set up the bot1
    	bots[1] = new Bot1(players.get(1),players.get(0),board,cube,match,ui.getInfoPanel());

    	((Francis)(bots[0])).setEnemyBot(bots[1]);
    	((Bot1)(bots[1])).setEnemyBot(bots[0]);
    }
    
    /**
     * Method to execute the whole game play [ 3 stages ] <br>
     * - Instantiation stage <br>
     * - Game turns stage (repeat until someone wins / quit command) <br>
     * - End Game stage <br>
     * Layer 2<br>
     * @throws InterruptedException
     */
    private void playGame() throws InterruptedException {
    	
    	startGame = true;
    	rollToStart();
    	playTurns();
        endCurrentGame();
        ui.display(); //  Update match score on boardPanel
    }
    
    /**
     * Boolean method to check if the players want to play new game in the same match<br>
     * Layer 2<br>
     * @return true if yes, else no
     * @throws InterruptedException
     */
    private boolean nextGame() throws InterruptedException {        
    	
    	while(true) {
        	
    		ui.promptPlayersNextGame();
    		
        	try {
        		String reply = ui.getString().toLowerCase().trim();
        	
        		if(reply.compareTo("yes") == 0) {		// play again
        			ui.display_PlayersWantNextGame();
        			nextGame = true;
        			resetGame();
        			ui.display_newGame();
        			return true;
        		}
        		else if(reply.compareTo("no") == 0 || reply.compareTo("quit") == 0) { 	// end game
        			
        			ui.display_PlayersWantQuitMatch();
        			nextGame = false;
        			newMatch = false;
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
     * Method to reset the game variables to initial states for a new match
     * Layer 2<br>
     * @throws InterruptedException
     */
    private void resetMatch() throws InterruptedException  {
    	
    	this.board.reset();						// Reset the board
    	this.realGame.reset();
    	this.match.reset();
    	this.cube.reset();
    	this.ui.clearInfo();
    	this.ui.display();
    	this.startGame					= false;
        this.nextGame 					= true;
        this.newMatch 					= false;
    }
    
    /**
     * Method to reset the game variables to initial states for a new game<br>
     * Thus nextMatch boolean value unchanged<br>
     * Layer 2<br>
     * @throws InterruptedException
     */
    private void resetGame() throws InterruptedException  {
    
    	this.board.reset();					// Reset the board
    	this.realGame.reset();
    	this.cube.reset();
    	this.ui.clearInfo();
    	this.ui.display();
    	this.startGame					= false;
        this.newMatch 					= false;
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
        
        boolean firstMove = true;      
        
        do {	// Repeat Game Turn
        		
            Player currentPlayer = players.getCurrent();
            ui.print_CurrentPlayer(currentPlayer.toString());
            realGame.setGameLength();
            
            // ----- DICE ROLL PART (Including Doubling Dice after first move) -----
            Dice currentDice = diceRollInTurns(firstMove, currentPlayer);
            firstMove = false;
            
            if(realGame.getResignedByDouble()) {
        		return;
        	}
            // ----- END OF DICE ROLL PART -----
            
            // ----- CHECKER MOVE PART -----
            checkerMoveInTurns(currentPlayer, currentDice); 
            
            // This if will be gotten rid of for training
            if(bots[currentPlayer.getId()] == null) {
            	// For real players (not bots)
                ui.display(); // Display information about the player's attempt in current game turn
                debug();
            }
            
            // ----- END OF CHECKER MOVE PART -----
            
            
            
            // ----- END TURN : switch current player -----
            if(!realGame.isOver()) {
            	players.advanceCurrentPlayer();
            }/*else {
            	System.out.println("Match is over");
            }*/
            
            ui.display();
            // ----- END OF END TURN -----
        } while (!realGame.isOver());    
    }
    
    /**
     * Game stage : End current game - check if the match is over<br>
     * Play next game until current match end<br>  
     * Reset startGame, update match score, display endGame<br>
     * Layer 3<br> 
     * @throws InterruptedException
     */
    private void endCurrentGame() throws InterruptedException {  
    	
    	startGame = false;
    	match.updateScores();
    	displayEndStage();
		ui.display_endGame();        					
        TimeUnit.SECONDS.sleep(2); 
    }
    
    /**
     * Method to play the dice roll part in each game turn<br>
     * It asks the current player whether to play doubling cube<br>
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
        	
        	// Check if the current player wants to offer double or redouble challenges
        	// Requirements : current game is able to use DC, DC hasn't been owned or Current player is owning DC
        	if(match.canDouble(currentPlayer) && (!cube.isOwned() || cube.getOwnerId() == currentPlayer.getId())) {
        		if(bots[currentPlayer.getId()] != null) {
        			promptDoubleCubeOption();   
        		}
        	}
        	
        	// Current player has no intent to quit current game
        	if(!realGame.resigned()) {
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

        	if(bots[currentPlayer.getId()] == null) {
        		// Player is not a bot
                ui.displayPlays(currentPlayer, possiblePlays);
                ui.promptCommand(currentPlayer);
                
                command = ui.getCommand(possiblePlays);
                ui.displayString("> " + command);		// Receive and display the given command 
                
                // Case 1 : Current player decides to quit the game 
                if (command.isQuit()) {
                	realGame.setResignedByCommand(currentPlayer);
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
        	else {
        		// player is a bot
                ui.displayPlays(currentPlayer, possiblePlays);
                ui.displayString("> " + bots[currentPlayer.getId()].getName() +" is about to choose move");
                
                // Get the bot to choose best move
                //(bots[currentPlayer.getId()]).getCommand(possiblePlays
                command = new Command(bots[currentPlayer.getId()].getCommand(possiblePlays),possiblePlays);
                
                ui.displayString("> " + command);		// Receive and display the given command 
                
                // Case 1 : Current player decides to quit the game 
                if (command.isQuit()) {
                	realGame.setResignedByCommand(currentPlayer);
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
            
        }
        
        TimeUnit.SECONDS.sleep(2);
        ui.displayString("");
        
        return command;
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
    		
    		// Case if the opponent accepts the offer
    		if(otherPlayerAcceptsDoubleOffer) {		
    			cube.accept(players.getEnemy());	// Opponent accepts the doubling challenge
    			ui.display(); 						// Update the double cube value
    			ui.print_doubleTheScore();
    			
    		} // Case if the opponent declines the offer - End current game
    		else {		
    			realGame.setResignedByDouble(players.getEnemy());	
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
    
    	if(!cube.isOwned())
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
			realGame.setResignedByCommand(players.getCurrent());
			quitGameByCommand();
			return false;
		}
        else								 // Else the current player won't	
           	return false;        
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
    		ui.promptOpponentToAcceptDouble(players.getEnemy().toString());
        	
    		try {
        		
        		String reply = ui.getString(); 
        		ui.displayString("> " + reply);							// Display user response
        		reply.toLowerCase().trim();
        		
        		if(reply.compareTo("yes") == 0) {						
        			return true;
        		}
        		else if(reply.compareTo("no") == 0) {
        			return false;
        		}
        		else if(reply.compareTo("quit") == 0) {
        			realGame.setResignedByCommand(players.get(currentPlayer_id));
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
     * Method to display related end stage message, ie. announce winner and sum of points earned so far<br>
     * @throws InterruptedException 
     */
    private void displayEndStage() throws InterruptedException {
    	
    	System.out.print("Enter End Stage\t:");
    	// END MATCH 
    	// Case if the current player uses the 'quit' command 
    	if(realGame.getResignedByCommand()) {
    		System.out.print(" 1 ");
    		if(startGame) {
    			System.out.print(" a ");
    			ui.displayString(players.getCurrent().toString() + " declare forfeit, thus " + players.getEnemy().toString() + " WIN THE GAME !!");
    		}
    		else {
    			System.out.print(" b ");
    			ui.displayString("Program closed");
    		}
    	} // Case if the players decide not to continue the game matches : Compare their score and winner is the one with higher score
    	else if(!nextGame) { 
    		System.out.print(" 2 ");
			if(match.getWinner() == null) { 
				System.out.print(" a ");
				ui.displayString("IT IS A DEUCE !!"); // Only happen when quit game suddenly or stop to continue the next match
			}
			else {
				System.out.print(" b ");
				ui.display_matchWinner(match.getWinner().toString());
			}
		}
    	// END A GAME 
    	// Case if a game end by rejecting double play (Opponent is the player who reject the doubling dice)
    	else if(realGame.getResignedByDouble()) {
    		System.out.print(" 3 ");
    		ui.print_rejectedDoubleTheScore(realGame.getWinner().toString(), players.getEnemy().toString(), realGame.getPoints()); 
    		
    	} // Case if a match end normally 	 
    	else if(realGame.isOver()) {    	
    			
    		System.out.print(" 4 ");
    		ui.display_roundWinner(players.getCurrent());
        }
    		
    	// Update match scores 
    	// DEBUG : Current Player not a winner
    	ui.display_CurrentMatchScores(players);
        
    	
    }
    	

    /**
     * Method for efficient debugging
     */
    private void debug() {
    	
    	System.out.println("[ In Game " + (match.getLength()) + " Turn "+ realGame.getGameLength() +" ]");
    	System.out.println("Current Player\t\t: " + players.getCurrent().toString());
    	System.out.println("Game Length\t\t: " + realGame.getGameLength());
    	System.out.println("Match Length\t\t: " + match.getLength());
    	System.out.println("Match Score\t\t: C - " + players.getCurrent().getScore() + " ; E - " + players.getEnemy().getScore());
    	if(cube.isOwned())
    		System.out.println("Player has doubing cube\t: " + cube.getOwner().toString());
    	if(realGame.getGameLength() < 3)
    		System.out.println("Start Match\t\t: " + startGame);
    	System.out.println("Doubling Cube Given\t: " + cube.isOwned() + " ["+ cube.getValue() + "]");
    	System.out.println("Exiting for command \t: " + realGame.getResignedByCommand());
    	System.out.println("Exiting for reject DP\t: " + realGame.getResignedByDouble());
    	System.out.println("Game Over\t\t: " + realGame.resigned());
    	System.out.println("Match Over\t\t: " + match.isOver());
    	System.out.println("Play next MATCH\t\t: " + nextGame);
    	System.out.println("Play new GAME\t\t: " + newMatch);
    	System.out.println("");
    }
}

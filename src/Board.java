/**
 * Board class that hold the details for the current board positions, performs moves and returns the list of legal moves
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class Board {
    
	/** Checkers positions to restore to initial position */
    private static final int[] RESET = {0,0,0,0,0,0,5,0,3,0,0,0,0,5,0,0,0,0,0,0,0,0,0,0,2,0};
    
    /** Checkers position when cheat code activated : Rearrange the checkers to given position */
    private static final int[][] CHEAT = {
        {13,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},  // checkers of Player 1 on board 
        {13,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},  // checkers of Player 2 on board	
    };

    private static final int[][] END = {
            {15,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},  // checkers of Player 1 on board 
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,1,0,2,4,3,2,0},  // checkers of Player 2 on board	
    };
    
    // ----- CONSTANTS -----
    public static final int BAR = 25;           	// Index of the BAR
    public static final int BEAR_OFF = 0;      		// Index of the BEAR OFF
    private static final int INNER_END = 6;     	// Index for the end of the inner board
    public static final int NUM_PIPS = 24;      	// Total number of pips on this board, EXECLUDING BAR and BEAR OFF
    public static final int NUM_SLOTS = 26;     	// Total number of slots on this board, INCLUDING BAR and BEAR OFF
    private static final int NUM_CHECKERS = 15;		// Total number of checkers per player
    // ----- END OF CONSTANTS -----
    
    /**
     * 2D array of checkers : <br>
     * - 1st index: is the player id <br>
     * - 2nd index is number pip number, 0 to 25 <br>
     * 		=> pip 0 is bear off<br>
     * 		=> pip 1 - 24 are on the main board <br> 
     * 			- pip 1 -> Ace point of the current player<br>
     * 			- pip 1 - 6 -> Opponent's home board<br>
     *			- pip 7 -> Bar point of the current player<br> 
     *			- pip 13-> Mid point of the current player<br>
     *			- pip 19-24 -> Player's home board<br> 
     * 		=> pip 25 is the bar/jail<br>
     * - the value in checkers is the number of checkers that the player has on the point<br>
     */
    private int[][] checkers;
    private Players players; 

    // ----- CONSTRUCTORS -----
    
    /** Constructor that used to start a new game round
     * @param players
     */
    Board(Players players) {
        
    	this.players = players;
        checkers = new int[Backgammon.NUM_PLAYERS][NUM_SLOTS];
        
        for (int player = 0 ; player < Backgammon.NUM_PLAYERS ; player++)  {
            
        	for (int pip=0; pip<NUM_SLOTS; pip++)   {
                checkers[player][pip] = RESET[pip];
            }
        }
    }

    Board(Players players, Board board) {
        this.players = players;
        this.checkers = new int[Backgammon.NUM_PLAYERS][NUM_SLOTS];
        for (int player=0; player<Backgammon.NUM_PLAYERS; player++)  {
            for (int pip=0; pip<NUM_SLOTS; pip++)   {
                this.checkers[player][pip] = board.checkers[player][pip];
            }
        }
    }
    
    // ----- END OF CONSTRUCTORS -----
    
    // ----- SUPPORTIVE METHODS -----
    
    /**
     * Method to reset the Board to the initial state 
     */
    public void resetTheBoard() {
        for (int player=0; player<Backgammon.NUM_PLAYERS; player++)  {
            for (int pip=0; pip<NUM_SLOTS; pip++)   {
                checkers[player][pip] = RESET[pip];
            }
        }
    }
    
    // TODO unknown use
    private int calculateOpposingPip(int pip) {
        return NUM_PIPS - pip + 1;
    }
    
    // TODO unknown use
    private int lastCheckerPip(Player player) {
        
    	int pip;
        
    	for (pip = BAR ; pip >= BEAR_OFF ; pip--) {
            if (checkers[player.getId()][pip] > 0) {
                break;
            }
        }
    	
        return pip;
    }
    
    /**
     * Method to check if the player with given ID has checker(s) in bar
     * @param playerID
     * @return true if yes, else false
     */
    public boolean isCheckersInBar(int playerID) {
    	if(checkers[playerID][BAR] > 0)
    		 return true;
    	return false;
    }
    
    /**
     * Method to check if the player with given ID has checker(s) been borne off
     * @param playerID
     * @return true if yes, else false
     */
    public boolean isCheckersBorneOff(int playerID) {    	
    	if(checkers[playerID][BEAR_OFF] > 0)
    		return true;
    	return false;
    }
    
    /**
     * Method to check if the player with given ID has checker(s) in opponent's home board
     * @param playerID
     * @return true if yes, else false
     */
    public boolean isCheckersInOpponentHome(int playerID) {
    	
    	// Loop through the opponent's home board
    	for(int i = 19 ; i < 24 ; i++) {
    		if(checkers[playerID][i] > 0)
    			return true;
    	}
    	
    	return false;
    }

    // ----- END OF SUPPORTIVE METHODS -----
    
    // ----- GETTERS ------
    
    /**
     * Method to get opponent's player ID 
     * @param player Current player
     * @return Opponent's player ID (If current player has ID is 0 -> opponent : 1 , vice versa)
     */
    private int getOpposingId(Player player) {
        if (player.getId() == 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Method to get the player's checker from given pip
     * @param player	Current Player
     * @param pip		Given pip that owned by the current player
     * @return	The checker that owned by the current player on the given pip
     */
    public int getNumCheckers(int player, int pip) {
        return checkers[player][pip];
    }
    
    /**
     * Method that define the condition to be a winner : All the checkers of the winner should be bear off
     * @return The instance of Player of the winner
     */
    public Player getWinner() {
        
    	Player winner = players.get(0);
    	
        if (checkers[0][BEAR_OFF] == NUM_CHECKERS) {
        	winner = players.get(0);
            
        } else if (checkers[1][BEAR_OFF] == NUM_CHECKERS) {
        	winner = players.get(1);
            
        }
        return winner;
    }
    
    // ----- END OF GETTERS ------
    
    // ----- CHECKER MOVES -----
    
    /**
     * Move the checker that owned by the given player
     * @param player	Current player
     * @param move		Move made
     */
    public void move(Player player, Move move) {
        checkers[player.getId()][move.getFromPip()]--;
        checkers[player.getId()][move.getToPip()]++;
        
        // bear off case-> add point for current player
        // TODO
        
        // Deal with hits
        if (move.getToPip() < BAR && move.getToPip() > BEAR_OFF &&
                checkers[getOpposingId(player)][calculateOpposingPip(move.getToPip())] == 1) {
            checkers[getOpposingId(player)][calculateOpposingPip(move.getToPip())]--;
            checkers[getOpposingId(player)][BAR]++;
        }
    }

    /**
     * Execute the checker move from the move list displayed in each game turn after dice roll
     * @param player	Current Player
     * @param play		Given game play
     */
    public void move(Player player, Play play) {
        for (Move move : play) {
            move(player,move);
        }
    }
    
    /**
     * Method that search for the plays that are possible with all of the movements that can be made based on the dice
     * @param player 	Current player
     * @param dice		Dice roll made by current player
     * @return	All the possible plays in the current game turn
     */
	public Plays getPossiblePlays(Player player, Dice dice) {
	   
	   Plays possiblePlays;
	   Movements movements = new Movements(dice);
	   
	   if (player.getDice().isDouble()) {
		   possiblePlays = findAllPlays(this,player,movements);
		   
	   } else {
		   
	       possiblePlays = findAllPlays(this,player,movements);
	       movements.reverse();
	       possiblePlays.add(findAllPlays(this,player,movements));
	   }
	   
	   possiblePlays.removeIncompletePlays();
	   possiblePlays.removeDuplicatePlays();
	   
	   return possiblePlays;
	}    
	
	/**
	 * Method to search for the plays that are possible with a given sequence of movements recursively
	 * @param board
	 * @param player
	 * @param movements
	 * @return
	 */
	private Plays findAllPlays(Board board, Player player, Movements movements) {
        
        Plays plays = new Plays();
        int fromPipLimit;
        // must take checkers from the bar first
        if (board.checkers[player.getId()][BAR] > 0) {
            fromPipLimit = BAR-1;
        } else {
            fromPipLimit = BEAR_OFF-1;
        }
        // search over the board for valid moves
        for (int fromPip=BAR; fromPip>fromPipLimit; fromPip--) {
            if (board.checkers[player.getId()][fromPip]>0) {
                int toPip = fromPip-movements.getFirst();
                Move newMove = new Move();
                Boolean isNewMove = false;
                if (toPip>BEAR_OFF) {
                    // check for valid moves with and without a hit
                    if (board.checkers[getOpposingId(player)][calculateOpposingPip(toPip)]==0) {
                        newMove = new Move(fromPip,toPip,false);
                        isNewMove = true;
                    } else if (board.checkers[getOpposingId(player)][calculateOpposingPip(toPip)]==1) {
                        newMove = new Move(fromPip,toPip,true);
                        isNewMove = true;
                    }
                } else {
                    // check for valid bear off
                    if (board.bearOffIsLegal(player) && (toPip==0 || (toPip<0 && board.lastCheckerPip(player)==fromPip))) {
                        newMove = new Move(fromPip,BEAR_OFF, false);
                        isNewMove = true;
                    }
                }
                // apply the move to the board and search for a follow on move
                if (isNewMove) {
                    if (movements.number()>1) {
                        Board childBoard = new Board(players,board);
                        childBoard.move(player,newMove);
                        Movements childMovements = new Movements(movements);
                        childMovements.removeFirst();
                        Plays childPlays = findAllPlays(childBoard, player, childMovements);
                        if (childPlays.number()>0) {
                            childPlays.prependAll(newMove);
                            plays.add(childPlays);
                        } else {
                            plays.add(new Play(newMove));
                        }
                    } else {
                        plays.add(new Play(newMove));
                    }
                }
            }
        }
        return plays;
    }

    /**
     * Method to execute cheat command : Move all the checkers to defined position
     */
    public void cheat() {

        for (int player = 0; player < Backgammon.NUM_PLAYERS; player++) {
        	for (int pip = 0; pip < NUM_SLOTS; pip++)   {
        			checkers[player][pip] = CHEAT[player][pip];
            }
        }
    }
    
    /**
     * Method to execute end command : Move all the checkers to defined position [ Testing Case ]
     */
    public void end() {

        for (int player = 0; player < Backgammon.NUM_PLAYERS; player++) {
        	for (int pip = 0; pip < NUM_SLOTS; pip++)   {
        			checkers[player][pip] = END[player][pip];
            }
        }
    }
    
    // ----- END OF CHECKER MOVES -----
    
    // ----- BOOLEAN METHODS -----
    
    /**
     * Method that shows if the current player meet the requirements of make a bear-off play
     * @param player Current Player
     * @return True if the player does, else false
     */
    private boolean bearOffIsLegal(Player player) {
        
    	int numberCheckersInInnerBoard = 0;
    	
        for (int pip = BEAR_OFF ; pip <= INNER_END ; pip++) {
            numberCheckersInInnerBoard += checkers[player.getId()][pip];
        }
        if (numberCheckersInInnerBoard == NUM_CHECKERS) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Method that check if the current player win the current game round
     * @param currentPlayer 
     * @return Yes if the current player meet the winning condition, else no
     */
    private boolean checkifCurrentPlayerWinMatch(int currentPlayer) {
    	
    	// Check if the current player has reached the point limit
    	if(checkers[currentPlayer][BEAR_OFF] == NUM_CHECKERS) {
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * Method to check if there is a winner for the current match
     * @param currentPlayer 
     * @return
     */
    public boolean isMatchOver(int currentPlayer) {
    	
        boolean matchOver = false;
        
        if (checkifCurrentPlayerWinMatch(currentPlayer)) {
        	matchOver = true;
        }
        return matchOver;
    }
    
    // ----- END OF BOOLEAN METHODS -----
}
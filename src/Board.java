/**
 * Board class that hold the details for the current board positions, performs moves and returns the list of legal moves
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class Board implements BoardAPI {
    
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
    public static final int ACE_POINT = 1;      		// Index of the ACE POINT
    private static final int INNER_END = 6;     	// Index for the end of the inner board
    private static final int OUTER_END = 18;
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
	 * Method to reset the board back to the initial state before the game starts
	 */
    public void reset() {
    	for(int player = 0 ; player < Backgammon.NUM_PLAYERS ; player++)
    		for(int pip = 0 ; pip < NUM_SLOTS ; pip++)
    			checkers[player][pip] = RESET[pip];
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
    private int getOpposingId(int playerId) {
        if (playerId == 0) {
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
    
    public int getNumbCheckersAtPosition(int pipPosition) {
    	if(checkers[0][pipPosition] > 0) {
    		return checkers[0][pipPosition];
    	}else if(checkers[1][pipPosition] > 0) {
    		return checkers[1][pipPosition];
    	}
    	else {
    		return 0;
    	}
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
     * @param playerID 	Player ID of the current player
     * @param dice		Dice roll made by current player
     * @return	All the possible plays in the current game turn
     */
	public Plays getPossiblePlays(int playerID, Dice dice) {
	   
	   Plays possiblePlays;
	   Movements movements = new Movements(dice);
	   
	   if (players.get(playerID).getDice().isDouble()) {
		   possiblePlays = findAllPlays(this, players.get(playerID), movements);
		   
	   } else {
		   
	       possiblePlays = findAllPlays(this, players.get(playerID), movements);
	       movements.reverse();
	       possiblePlays.add(findAllPlays(this, players.get(playerID), movements));
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
    
    @Override
	public boolean lastCheckerInInnerBoard(int playerID) {
		return findLastChecker(players.get(playerID)) <= INNER_END;
	}

	@Override
	public boolean lastCheckerInOpponentsInnerBoard(int playerID) {
        return findLastChecker(players.get(playerID)) > OUTER_END;
	}
	
	private int findLastChecker(Player player) {
        int pip;
        for (pip = BAR ; pip >= BEAR_OFF ; pip--) {
            if (checkers[player.getId()][pip] > 0) {
                break;
            }
        }
        return pip;
    }
	
	@Override
	public boolean allCheckersOff(int playerID) {
		return checkers[playerID][BEAR_OFF] == NUM_CHECKERS;
	}

	@Override
	public boolean hasCheckerOff(int playerID) {
		return checkers[playerID][BEAR_OFF] > 0;
	}

    // ----- END OF BOOLEAN METHODS -----
    
	@Override
	public int[][] get() {
		
		// duplicate prevents the Bot moving the checkers
        int[][] duplicateCheckers = new int[Backgammon.NUM_PLAYERS][NUM_SLOTS];
        for (int i=0; i<checkers.length; i++) {
            for (int j=0; j<checkers[i].length; j++) {
                duplicateCheckers[i][j] = checkers[i][j];
            }
        }
        return duplicateCheckers;
	}

	@Override
	public Plays getPossiblePlays(int playerId, Dice dice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean lastCheckerInInnerBoard(int playerId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lastCheckerInOpponentsInnerBoard(int playerId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean allCheckersOff(int playerId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCheckerOff(int playerId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	


	
}
import java.util.concurrent.ThreadLocalRandom;

/**
 * Game Bot Class
 * @author Ee En Goh
 *
 */
public class Francis implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
    // variables to keep track of so can adjust the weight
    private int botLosesInARow = 0;
    private boolean inTrainingMode = true;
    
    // Weight adjustment variables
    private double weightAdjustment_Min = -0.0005;
    private double weightAdjustment_Max = 0.005;
    // Weights
    private int pipCountDifference_weight = 0;
    
    private int blockBlotDif_weight = 0;
    
    private int botBlocksHome_weight = 0;
    private int opponentsBlocksHome_weight = 0;
    
    private int botBlockOpponentHome_weight = 0;
    private int opponentBlockBotHome_weight = 0;
    
    // for later: private int differenceBetweenBlockedCheckers_weight = 0;	// difference between pips that are trapped behind enemy pips
    // for later: private int differenceBetweenEscapedCheckers_weight = 0;	// same as above, but for checkers that have escaped
    
    //private int differenceBetweenPipCountInJail_weight = 0;
    private int botPipCountInJail_weight = 0;
    private int opponentsPipCountInJail_weight = 0;
    
    //private int differenceBetweenPipCountsBearedOff_weight = 0;
    private int botPipCountBearedOff_weight = 0;
    private int opponentsPipCountBearedOff_weight = 0;
    
    private int botPipCountInHome_weight = 0;
    private int opponentsPipCountInHome_weight = 0;

    // END OF Weights
    
    private Board copyOfBoard;

    Francis(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
        
    }

    /**
     * return the bot name (must match the class name)
     */
    public String getName() {
        return "Francis"; 
    }

    /**
     * TODO
     */
    public String getCommand(Plays possiblePlays) {
        // Add your code here
    	

    	// Get score for current board(without any moves)
    	int rootScore = getScoreForBoard(board.get());
    	int currentHighestScore = 0;
    	Play playWithHighestScore;
    	
    	int[][] originalBoard = board.get();
    	
    	boolean firstScoreGotten = false;
    	
    	
    	// Now calculate score for each play
    	for (Play currentPlay : possiblePlays) {
			
    		// Get the board after the move
    		int[][] shadowBoard_AfterMove = board.getBoardAfterPlay(originalBoard.clone(), currentPlay, me.getId());
    		
    		// Get the new boards score
    		int scoreOfBoardAfterMove = getScoreForBoard(shadowBoard_AfterMove);
    		
    		if(!firstScoreGotten) {
    			firstScoreGotten = true;
    			currentHighestScore = scoreOfBoardAfterMove;
    			playWithHighestScore = currentPlay;
    		}
    		else {
    			if(scoreOfBoardAfterMove > currentHighestScore) {
    				currentHighestScore = scoreOfBoardAfterMove;
        			playWithHighestScore = currentPlay;
    			}
    		}
    		
		}
    	
    	// in the end, the highest scored move, and the move that will achieve the highest score
    	
    	
    	
    	
    	
    	
    	
        return "1";
    }
    
    // Helpers for getCommand
    
    private boolean getIfPointIsInPlayersHome(int point) {
    	if(point > 0 && point <= 6) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean getIfPointIsInPlayersEnemysHome(int point) {
    	
    	if(point > 18  && point <= 24) {
    		return true;
    	}
    	return false;
    }
    
    
    // End of helpers for getCommand

    /**
     * TODO
     */
    public String getDoubleDecision() {
        // Add your code here
        return "n";
    }
    
    
    
    // evaluating the stats
    private int getScoreForBoard(int[][] theBoard) {
    	
    	int blockBlotDif = 0;
    	int blocks = 0;
    	int blots = 0;
    	
    	int botPieceCountInHome = 0;
    	int opponentsPieceCountInHome = 0;
    	
    	int botTotalPipCount = 0;
    	int opponentTotalPipCount = 0;
    	
    	int botHomeBlockCount = 0;
    	int opponentsHomeBlockCount = 0;
    	
    	int botBlockCountOpponentsHome = 0;
    	int opponentsBlockCountBotsHome = 0;
    	
    	
    	
    	// Search through the points of the board
    	
    	int currentPointer = 1;
    	int adjustment = +1;
    	int stopPoint = 24;
    	
    	while(currentPointer != stopPoint) {
    	
    		if(theBoard[me.getId()][currentPointer]>0) {
    			// then bot has pieces on pip position
        		// Evaluate bot variables
    			int pointsPieceCount = theBoard[me.getId()][currentPointer];
    			
    			
    			// Blocks and blots
    			if(pointsPieceCount > 1) {
    				blocks++;
    			}
    			
    			// In bots home
    			if(getIfPointIsInPlayersHome(currentPointer)) {
    				// Then bot is in bots home
        			if(pointsPieceCount > 1) {
        				botHomeBlockCount++;
        			}
        			botPieceCountInHome += pointsPieceCount;
    			}
    			
    			
    			// In opponents home
    			if(getIfPointIsInPlayersEnemysHome(currentPointer)) {
    				// Then bot is in opponents home
        			if(pointsPieceCount > 1) {
        				botBlockCountOpponentsHome++;
        			}
    				
    			}
    			    			
    			botTotalPipCount += currentPointer;
    			
    		}
    		if(theBoard[opponent.getId()][currentPointer] > 0) {
    			// then opponent has pieces on pip position
        		// Evaluate opponents variables
        		
    			int pointsPieceCount = theBoard[opponent.getId()][currentPointer];
    			
    			// Blocks and blots
    			if(pointsPieceCount == 1) {
    				blots++;
    			}
    			
    			// In bots home
    			if(getIfPointIsInPlayersHome(currentPointer)) {
    				// Then bot is in bots home
        			if(pointsPieceCount > 1) {
        				opponentsHomeBlockCount++;
        			}
        			opponentsPieceCountInHome += pointsPieceCount;
    			}
    			
    			// In opponents home
    			if(getIfPointIsInPlayersEnemysHome(currentPointer)) {
    				// Then bot is in opponents home
        			if(pointsPieceCount > 1) {
        				opponentsBlockCountBotsHome++;
        			}
    				
    			}
    			    			
    			opponentTotalPipCount += currentPointer;
    			
    		}else {
    			// No pieces on pip position
    		}
    	
    		
    		currentPointer += adjustment;
    		
    	}
    	
    	// Check how many points player has scored (pieces beared off)
    	int botsPointsScored = theBoard[me.getId()][0];
    	int opponentsPointsScored = theBoard[opponent.getId()][0];
    	
    	int botPiecesInJail = theBoard[me.getId()][25];
    	int opponentsPiecesInJail = theBoard[opponent.getId()][25];

    	// Get:
    	
    	// Block Blot difference
    	blockBlotDif = blocks - blots;
    	
    	// Difference between bot and opponent pip count
    	int pipCountDifference = botTotalPipCount - opponentTotalPipCount;
    	
    	
    	// FINNISHED ASSIGNEING THE VARIABLES
    	// Now must get the score
    	
    	int totalScore = 0;
    	
    	totalScore += 
    	((blockBlotDif * blockBlotDif_weight) + (pipCountDifference * pipCountDifference_weight)
    	+ (botHomeBlockCount * botBlocksHome_weight) + (opponentsHomeBlockCount * opponentsBlocksHome_weight) 
    	+ (botBlockCountOpponentsHome * botBlockOpponentHome_weight) + (opponentsBlockCountBotsHome * opponentBlockBotHome_weight)
    	+ (botPieceCountInHome * botPipCountInHome_weight) + (opponentsPieceCountInHome * opponentsPipCountInHome_weight)
    	+ (botsPointsScored * botPipCountBearedOff_weight) + (opponentsPointsScored * opponentsPipCountBearedOff_weight)
    	+ (botPiecesInJail * botPipCountInJail_weight) + (opponentsPiecesInJail * opponentsPipCountInJail_weight));
    	
    	
    	return totalScore;
    }
    
    
    // For training
    
    
    public void botLoses() {
    	if(inTrainingMode) {
    		botLosesInARow++;
        	if(botLosesInARow == 3) {
        		// Exchange the weights between the bots
        		
        		
        		// End of changing the weights between bots
        		botLosesInARow = 0;	// Reset
        	}
        	else {
        		// Adjust the weights by small amount as described in document
        		double weightAdjustment = ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);
        		// positive weights
        		pipCountDifference_weight -= weightAdjustment;
        		blockBlotDif_weight -= weightAdjustment;
        		botBlocksHome_weight -= weightAdjustment;
        		botBlockOpponentHome_weight -= weightAdjustment;
        		botPipCountInJail_weight -= weightAdjustment;
        		botPipCountInHome_weight -= weightAdjustment;
        		botPipCountBearedOff_weight -= weightAdjustment;
        		
        		// Negative weights
        		opponentsBlocksHome_weight += weightAdjustment;
        		opponentBlockBotHome_weight += weightAdjustment;
        		opponentsPipCountInJail_weight += weightAdjustment;
        		opponentsPipCountInHome_weight += weightAdjustment;
        		opponentsPipCountBearedOff_weight += weightAdjustment;
        	}
    	}
    }
    
    public void botWins() {
    	if(inTrainingMode) {
    		// Adjust the weight by small amount as described in document
    		
    		double weightAdjustment = ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);
    		//add a small random amount (could be slightly negative) to the 
    		//positive weights and subtract a small amount from the negative weights (could be slightly negative
    		
    		// positive weights
    		pipCountDifference_weight += weightAdjustment;
    		blockBlotDif_weight += weightAdjustment;
    		botBlocksHome_weight += weightAdjustment;
    		botBlockOpponentHome_weight += weightAdjustment;
    		botPipCountInJail_weight += weightAdjustment;
    		botPipCountInHome_weight += weightAdjustment;
    		botPipCountBearedOff_weight += weightAdjustment;
    		
    		
    		
    		// Negative weights
    		opponentsBlocksHome_weight -= weightAdjustment;
    		opponentBlockBotHome_weight -= weightAdjustment;
    		opponentsPipCountInJail_weight -= weightAdjustment;
    		opponentsPipCountInHome_weight -= weightAdjustment;
    		opponentsPipCountBearedOff_weight -= weightAdjustment;
    		
    	}
    }
    
    public void swapWeightsWithOtherPlayer(BotAPI otherBot) {
    	
    }
    
    public int[] getWeights() {
    	int[] weights = {pipCountDifference_weight,blockBlotDif_weight,botBlocksHome_weight,botBlockOpponentHome_weight,botPipCountInJail_weight
    			,botPipCountInHome_weight,botPipCountBearedOff_weight, opponentsBlocksHome_weight, opponentBlockBotHome_weight,opponentsPipCountInJail_weight
    			,opponentsPipCountInHome_weight,opponentsPipCountBearedOff_weight};
    	return weights;
    }
    
    public static void swapBotsWeights(BotAPI bot0, BotAPI bot1) {
    	
    }
    
    
    
    
}

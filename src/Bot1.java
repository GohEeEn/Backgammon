import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Bot1 implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects
	
	private final boolean TRAINING = true;
    private static final String WEIGHT_FILE = "Steven_WeightsForScoring.txt";
    
    private Double referenceScoreForBot;

    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
    private BotAPI opponentBot;
    
    // variables to keep track of so can adjust the weight
    private int botLosesInARow = 0;
    private boolean inTrainingMode = true;
    
    // Weight adjustment variables
    private double weightAdjustment_Min = -0.0005;
    private double weightAdjustment_Max = 0.005;
    // Weights
    private static final int AMOUNTOFWEIGHTS = 14;
    private Double checkersInJail_weight = 0.0;
    
    private Double pipCountDifference_weight = 0.0;
    
    private Double blockBlotDif_weight = 0.0;
    
    private Double botBlocksHome_weight = 0.0;
    private Double opponentsBlocksHome_weight = 0.0;
    
    private Double botBlockOpponentHome_weight = 0.0;
    private Double opponentBlockBotHome_weight = 0.0;
    
    private Double allPiecesInHome_weight = 0.0;
    
    // for later: private int differenceBetweenBlockedCheckers_weight = 0;	// difference between pips that are trapped behind enemy pips
    // for later: private int differenceBetweenEscapedCheckers_weight = 0;	// same as above, but for checkers that have escaped
    
    //private int differenceBetweenPipCountInJail_weight = 0;
    private Double botPipCountInJail_weight = 0.0;
    private Double opponentsPipCountInJail_weight = 0.0;
    
    //private int differenceBetweenPipCountsBearedOff_weight = 0;
    private Double botPipCountBearedOff_weight = 0.0;
    private Double opponentsPipCountBearedOff_weight = 0.0;
    
    private Double botPipCountInHome_weight = 0.0;
    private Double opponentsPipCountInHome_weight = 0.0;
    
    private Double botScore = 0.0;
    private Double opponentScore = 0.0;
            
    // END OF Weights
    
    Bot1(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
        
        this.retrieveWeights();
        
        referenceScoreForBot = this.getScoreForBoard(board.get());
    }

    /**
     * return the bot name (must match the class name)
     */
    public String getName() {
        return "Bot1"; 
    }
    
    public static String getTheName() {
    	return "Bot1"; 
    }

    /**
     * TODO
     */
    public String getCommand(Plays possiblePlays) {
        // Add your code here
    	

    	// Get score for current board(without any moves)
    	Double currentHighestScore = 0.0;
    	Play playWithHighestScore;
    	
    	int[][] originalBoard = board.get();
    	
    	boolean firstScoreGotten = false;
    	int currentPlayPointer = -1;
    	// Now calculate score for each play
    	for (Play currentPlay : possiblePlays) {
			
    		// Get the board after the move
    		int[][] shadowBoard_AfterMove = getBoardAfterPlay(originalBoard.clone(), currentPlay, me.getId());
    		
    		// Get the new boards score
    		Double scoreOfBoardAfterMove = getScoreForBoard(shadowBoard_AfterMove);
    		
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
    		
    		currentPlayPointer++;
		}
    	
    	// in the end, the highest scored move, and the move that will achieve the highest score
        return ("" + currentPlayPointer);
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
    public double getScoreForBoard(int[][] theBoard) {
    	
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
    	
    	// SPECIAL CASE: CHECK IF BOT HAS PIECES IN JAIL
    	Double botBonus = 0.0;
    	Double opponentBonus = 0.0;
    	if(board.getNumCheckers(me.getId(), 25) > 0) {
    		// bot is in jail, so some blocks inopponents home will effect more
    		botBonus += (opponentsHomeBlockCount * checkersInJail_weight * board.getNumCheckers(me.getId(), 25));
    	}
    	
    	// SPECIAL CASE: CHECK IF BOT HAS PIECES IN ALL PIECES IN HOME
    	else if(board.lastCheckerInInnerBoard(me.getId())) {
    		// Bot has all pieces in home section of board
    		// loop through home of bot and "weigh"
    		int totalWeight = 0;
    		for(int i = 1;i <= 6;i++) {
    			int weightForPipPosition = (7-i)*theBoard[me.getId()][i];
    			totalWeight += weightForPipPosition;
    		}
    		
    		botBonus += (totalWeight * allPiecesInHome_weight);
    		
    	}
    	    	
    	Double totalScore = 0.0;
    	
    	botScore = (botHomeBlockCount * botBlocksHome_weight) + (botBlockCountOpponentsHome * botBlockOpponentHome_weight)
    			+ (botPieceCountInHome * botPipCountInHome_weight) + (botsPointsScored * botPipCountBearedOff_weight) 
    			+ (botPiecesInJail * botPipCountInJail_weight) + botBonus;
    	
    	opponentScore = (opponentsHomeBlockCount * opponentsBlocksHome_weight) + (opponentsBlockCountBotsHome * opponentBlockBotHome_weight)
    			+ (opponentsPieceCountInHome * opponentsPipCountInHome_weight) + (opponentsPointsScored * opponentsPipCountBearedOff_weight)
    			+ (opponentsPiecesInJail * opponentsPipCountInJail_weight) + opponentBonus;
    	
    	totalScore += 
    	((blockBlotDif * blockBlotDif_weight) + (pipCountDifference * pipCountDifference_weight)
    	+ botScore + opponentScore);
    	
    	
    	return totalScore;
    }
    
    
    // For training
    
    
    public void botLoses() {
    	if(inTrainingMode) {
    		botLosesInARow++;
        	if(botLosesInARow == 3) {
        		// Exchange the weights between the bots
        		
        		swapBotsWeightsWithOpponentBot(opponentBot);
        		
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
    		allPiecesInHome_weight += weightAdjustment;
    		
    		
    		
    		// Negative weights
    		opponentsBlocksHome_weight -= weightAdjustment;
    		opponentBlockBotHome_weight -= weightAdjustment;
    		opponentsPipCountInJail_weight -= weightAdjustment;
    		opponentsPipCountInHome_weight -= weightAdjustment;
    		opponentsPipCountBearedOff_weight -= weightAdjustment;
    		checkersInJail_weight -= weightAdjustment;
    		
    	}
    }
    
    public void swapWeightsWithOtherPlayer(Double[] botWeights) {
		// positive weights
		pipCountDifference_weight = botWeights[0];
		blockBlotDif_weight = botWeights[1];
		botBlocksHome_weight = botWeights[2];
		botBlockOpponentHome_weight = botWeights[3];
		botPipCountInJail_weight = botWeights[4];
		botPipCountInHome_weight = botWeights[5];
		botPipCountBearedOff_weight = botWeights[6];
		
		
		
		// Negative weights
		opponentsBlocksHome_weight = botWeights[7];
		opponentBlockBotHome_weight = botWeights[8];
		opponentsPipCountInJail_weight = botWeights[9];
		opponentsPipCountInHome_weight = botWeights[10];
		opponentsPipCountBearedOff_weight = botWeights[11];
		checkersInJail_weight = botWeights[12];
		
		// others 
		allPiecesInHome_weight = botWeights[13];
		
    }
    
    // ONLY FOR TRAINING
    public Double[] getWeights() {
    	Double[] weights = {pipCountDifference_weight,blockBlotDif_weight,botBlocksHome_weight,botBlockOpponentHome_weight,botPipCountInJail_weight
    			,botPipCountInHome_weight,botPipCountBearedOff_weight, opponentsBlocksHome_weight, opponentBlockBotHome_weight,opponentsPipCountInJail_weight
    			,opponentsPipCountInHome_weight,opponentsPipCountBearedOff_weight,checkersInJail_weight,allPiecesInHome_weight};
    	return weights;
    }
    
    private void swapBotsWeightsWithOpponentBot(BotAPI opponentBot) {
    	Double[] bot0_weights = this.getWeights();
    	Double[] bot1_weights = opponentBot.getWeights();
    	
    	this.swapWeightsWithOtherPlayer(bot1_weights);
    	opponentBot.swapWeightsWithOtherPlayer(bot0_weights);
    }
    
    public void setEnemyBot(BotAPI opponentBot) {
    	this.opponentBot = opponentBot;
    }
    
    public void saveWeights() {
		// Write weights to file
        try {
        	//OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(WEIGHT_FILE)));
        	FileWriter out = new FileWriter(WEIGHT_FILE);
        	Double[] weights = this.getWeights();
			String output = "";
			for (Double weight : weights) {
				output += weight + "\n";
			}
			System.out.println(output);
			
			out.write(output);
			out.flush();
			out.close();
			System.out.println("hel");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void retrieveWeights() {
    	// retrieve the weights
        try {
        	BufferedReader bf1 = new BufferedReader(new FileReader(WEIGHT_FILE));
        	Double[] weights = new Double[AMOUNTOFWEIGHTS];
			for(int i = 0; i < AMOUNTOFWEIGHTS;i++) {
				weights[i] = Double.parseDouble(bf1.readLine());
			}
			
			swapWeightsWithOtherPlayer(weights);
			bf1.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    // END ONLY FOR TRAINING
    
    // SEEING THE BOARD
    
    private int[][] getBoardAfterPlay(int[][] shadowBoard, Play playersMove, int playersNumber) {
		for (Move move : playersMove) {
			shadowBoard = getBoardAfterMove(shadowBoard,move,playersNumber);
		}
		return shadowBoard;
	}
	
	private int[][] getBoardAfterMove(int[][] shadowBoard, Move playersMove, int playersNumber){
		
		shadowBoard[playersNumber][playersMove.getFromPip()]--;
		shadowBoard[playersNumber][playersMove.getToPip()]++;
        
        // bear off case-> add point for current player
        // TODO
        
        // Deal with hits
        if (playersMove.getToPip() < Board.BAR && playersMove.getToPip() > Board.BEAR_OFF &&
        		shadowBoard[opponent.getId()][calculateOpposingPip(playersMove.getToPip())] == 1) {
        	shadowBoard[opponent.getId()][calculateOpposingPip(playersMove.getToPip())]--;
        	shadowBoard[opponent.getId()][Board.BAR]++;
        }
		
		return shadowBoard;
	}
    
    private int calculateOpposingPip(int pip) {
        return Board.NUM_PIPS - pip + 1;
    }
          
    // SEEING THE BOARD
    
}

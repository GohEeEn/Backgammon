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
    
    private boolean botIsAskingToDouble = false;
    
    private double referenceScoreForBoard;
    private double referenceScoreForBot;
    private double referenceScoreForOpponent;

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
    private static final int AMOUNTOFWEIGHTS = 19;
    
    
    private double checkersInJailBot_weight = 0.0;		// Negative 
    private double checkersInJailOpponent_weight = 0.0;	// Positive

    private double pipCountDifference_weight = 0.0;		// Positive	(for both bot and player) 

    private double blockBlotDifBot_weight = 0.0;		// Positive
    private double blockBlotDifOpponent_weight = 0.0;	// Negative
    
    private double botBlocksHome_weight = 0.0;			// Positive
    private double opponentsBlocksHome_weight = 0.0;	// Negative

    private double botBlockOpponentHome_weight = 0.0;		// Positive
    private double opponentBlockBotHome_weight = 0.0;		// Negative

    private double allPiecesInHomeBot_weight = 0.0;			// Positive	
    private double allPiecesInHomeOpponent_weight = 0.0;	// Negative
    
    private double lengthOfBlockChainsBot_weight = 0.0;			// Positive TODO: THIS IS JUST A TEST
    private double lengthOfBlockChainsOpponent_weight = 0.0;			// negative TODO: THIS IS JUST A TEST

    // for later: private int differenceBetweenBlockedCheckers_weight = 0;	// difference between pips that are trapped behind enemy pips
    // for later: private int differenceBetweenEscapedCheckers_weight = 0;	// same as above, but for checkers that have escaped

    //private int differenceBetweenPipCountInJail_weight = 0;
    private double botPipCountInJail_weight = 0.0;			// Negative
    private double opponentsPipCountInJail_weight = 0.0;	// Positive

    //private int differenceBetweenPipCountsBearedOff_weight = 0;
    private double botPipCountBearedOff_weight = 0.0;		// Positive
    private double opponentsPipCountBearedOff_weight = 0.0;	// Negative

    private double botPipCountInHome_weight = 0.0;			// Positive
    private double opponentsPipCountInHome_weight = 0.0;	// Negative

    private double botScore = 0.0;
    private double opponentScore = 0.0;





    // END OF Weights

    Bot1(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;

        this.retrieveWeights();

        
        // Get reference score
        // This will be done by getting the score for the bot at board set up
        // this will be roughly == to being 50% chance of failure and loss (I am going to ignore the way who roles first effects there odds)
        // Convert this number to 100% and this is some rough reference based on the current board set up
        referenceScoreForBoard = this.getScoreForBoard(board.get());
        referenceScoreForBot = botScore;
        referenceScoreForOpponent = opponentScore;
        referenceScoreForBoard = referenceScoreForBoard * 2;
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
    	
    	// Ask if want to double
    	
    	/*		UNCOMMENT WHEN SUBMITTING
    	botIsAskingToDouble = true;
    	String seeIfBotWantsToAskForDouble = getDoubleDecision();
    	if(seeIfBotWantsToAskForDouble.equals("double")) {
    		// ask for a double
    		botIsAskingToDouble = false;
    		return seeIfBotWantsToAskForDouble;
    	}
    	botIsAskingToDouble = false;
    	*/
    	
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

    /*		breaks API
    public String initDouble() {
		// TODO Auto-generated method stub

    	if(getScoreForBoard(board.get()) > opponentBot.getScoreForBoard(board.get()) * 10)
    		return "double";
		return "no";
	}
	*/

    /**
     * TODO
     */
    public String getDoubleDecision() {
    	
    	// This method will either be called from getCommand or it will called to ask if bot wants to accept a double decision
    	// to distinguish between which have boolean variable: botBeingAskedToDouble

    	// Match Stage : 3 kind of stages -> Normal, Both Players 2 points from winning, Post Crawford

		// bot is considering asking
    	// Stage 1 : Both Players 2 points from winning
    	if(me.getScore() - 2 == match.getMatchPoint() && me.getScore() - 2 == match.getMatchPoint()) {
    		// bot is being asked to double by other player
    		if(percentageChanceOfsuccess(getScoreForBoard(board.get())) >= 50 && percentageChanceOfsuccess(getScoreForBoard(board.get())) <= 75) {
    			if(!botIsAskingToDouble) {
    				return "yes";
    			}
    			return "double";
    		}
    		else if(percentageChanceOfsuccess(getScoreForBoard(board.get())) >= 75) {
    			return "no";
    		}
    		else {
    			if(!botIsAskingToDouble) {
    				return "yes";
    			}
    			return "double";
    		}

    	} // Normal Stage
    	
    	else {
    		if(!botIsAskingToDouble) {
    			// Then bot is the one being asked
    		}
    		// Bot is not being asked to double, and instead is going to choose for himself
    		double percentageChanceOfSuccess = percentageChanceOfsuccess(getScoreForBoard(board.get()));
    		if(percentageChanceOfSuccess < 66)
    			return "no";
    		else if(percentageChanceOfSuccess >= 66 && percentageChanceOfSuccess < 75) {
    			if(!botIsAskingToDouble) {
    				return "yes";
    			}
    			return "double";
    		}
    		else if(percentageChanceOfSuccess >= 75) {
    			if(!botIsAskingToDouble) {
    				return "yes";
    			}
    			return "double";
    		}
    		else
    			return "no";
    	}
    	
    	
    	
    }
    
    private double percentageChanceOfsuccess(double currentScore) {
    	// Get the percentage in respect to references (board, bot and opponent)
    	double remainder_boardScore = currentScore - referenceScoreForBoard;
    	double remainderbotScore = botScore - referenceScoreForBot;
    	double remainder_opponentScore = opponentScore - referenceScoreForOpponent;
    	
    	double percentageChangeForBoardScore = (remainder_boardScore/referenceScoreForBoard) * 100;
    	double percentageChangeForBotScore = (remainderbotScore/referenceScoreForBot) * 100;
    	double percentageChangeForOpponentScore = (remainder_opponentScore/referenceScoreForOpponent) * 100;
    	
    	double averagePercentageChange = (percentageChangeForBoardScore + percentageChangeForBotScore + percentageChangeForOpponentScore)/3;
    	
    	return averagePercentageChange;
    }


    /**
     * Calculate the total board score of this bot, based on the position of owned checkers
     * @param theBoard	The duplication of board pips
     * @return			Board score of this bot
     */
    public double getScoreForBoard(int[][] theBoard) {

    	/** The difference between the number of blocks(pip with at least 1 pip) and blots(empty pip) on board */
    	int blockBlotDif_bots = 0;
    	int blockBlotDif_opponents = 0;
    	int blocks_bots = 0;
    	int blots_opponents = 0;
    	int blocks_opponents = 0;
    	int blots_bots = 0;
    	

    	int botPieceCountInHome = 0;
    	int opponentsPieceCountInHome = 0;

    	int botTotalPipCount = 0;
    	int opponentTotalPipCount = 0;

    	int botHomeBlockCount = 0;
    	int opponentsHomeBlockCount = 0;

    	int botBlockCountOpponentsHome = 0;
    	int opponentsBlockCountBotsHome = 0;
    	
    	// TODO: FOR TEST (REMOVE IF DOESNT WORK)
    	int lengthOfCurrentBlockChain_bots = 0;
    	int lengthOfCurrentBlockChain_opponents = 0;
    	boolean previousBlockInPoint_bots = false;
    	boolean previousBlockInPoint_opponents = false;
    	int totalScoreForBlockChains_Bots = 0;
    	int totalScoreForBlockChains_Opponents = 0;

    	// Search through the points of the board

    	int currentPointer = 1;	// Search starts from ace point
    	int adjustment = +1;
    	int stopPoint = 24;

    	while(currentPointer != stopPoint) {

    		if(theBoard[me.getId()][currentPointer]>0) {
    			// then bot has pieces on pip position
        		// Evaluate bot variables
    			int pointsPieceCount = theBoard[me.getId()][currentPointer];

    			// Blocks and blots
    			if(pointsPieceCount == 1) {
    				// bot blot
    				blots_bots++;
    			}
    			
    			if(pointsPieceCount > 1) {
    				// Bot block
    				if(previousBlockInPoint_bots) {
    					// There is a chain/length of blocks (as previous point had a bot block)
    					lengthOfCurrentBlockChain_bots += 1;
    				}else {
    					// No chain/length of bot blocks
    					lengthOfCurrentBlockChain_bots += 1;
    					previousBlockInPoint_bots = true;
    				}
    				blocks_bots++;
    				
    			}
    			else {
    				// not a bot block, and so check if have to end chain
    				if(previousBlockInPoint_bots) {
    					// Then current point has no bot blocks, therefor calculate the score for the length of the block chain
    					totalScoreForBlockChains_Bots += (lengthOfCurrentBlockChain_bots);
    					
    					lengthOfCurrentBlockChain_bots = 0; 	// reset the length of the current bot block chain
    					previousBlockInPoint_bots = false;
    				}
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
    				// bot blot
    				blots_opponents++;
    			}
    			
    			if(pointsPieceCount > 1) {
    				// Bot block
    				if(previousBlockInPoint_opponents) {
    					// There is a chain/length of blocks (as previous point had a bot block)
    					lengthOfCurrentBlockChain_opponents += 1;
    				}else {
    					// No chain/length of bot blocks
    					lengthOfCurrentBlockChain_opponents += 1;
    					previousBlockInPoint_opponents = true;
    				}
    				blocks_opponents++;
    				
    			}
    			else {
    				// not a bot block, and so check if have to end chain
    				if(previousBlockInPoint_opponents) {
    					// Then current point has no bot blocks, therefor calculate the score for the length of the block chain
    					totalScoreForBlockChains_Opponents += (lengthOfCurrentBlockChain_opponents);
    					
    					lengthOfCurrentBlockChain_opponents = 0; 	// reset the length of the current bot block chain
    					previousBlockInPoint_opponents = false;
    				}
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
    	blockBlotDif_bots = blocks_bots - blots_opponents;
    	blockBlotDif_opponents = blocks_opponents - blots_bots;

    	// Difference between bot and opponent pip count
    	int pipCountDifference = botTotalPipCount - opponentTotalPipCount;

    	// FINNISHED ASSIGNEING THE VARIABLES
    	// Now must get the score

    	// SPECIAL CASE: CHECK IF BOT HAS PIECES IN JAIL
    	Double botBonus = 0.0;
    	Double opponentBonus = 0.0;
    	if(board.getNumCheckers(me.getId(), 25) > 0) {
    		// bot is in jail, so some blocks inopponents home will effect more
    		botBonus += (opponentsHomeBlockCount * checkersInJailBot_weight * board.getNumCheckers(me.getId(), 25));
    	}
    	
    	if(board.getNumCheckers(opponent.getId(), 25) > 0) {
    		// bot is in jail, so some blocks inopponents home will effect more
    		opponentBonus += (botHomeBlockCount * checkersInJailOpponent_weight * board.getNumCheckers(opponent.getId(), 25));
    	}

    	// SPECIAL CASE: CHECK IF BOT HAS PIECES IN ALL PIECES IN HOME
    	if(board.lastCheckerInInnerBoard(me.getId())) {
    		// Bot has all pieces in home section of board
    		// loop through home of bot and "weigh"
    		int totalWeight = 0;
    		for(int i = 1;i <= 6;i++) {
    			int weightForPipPosition = (7-i)*theBoard[me.getId()][i];
    			totalWeight += weightForPipPosition;
    		}

    		botBonus += (totalWeight * allPiecesInHomeBot_weight);

    	}
    	if(board.lastCheckerInInnerBoard(opponent.getId())) {
    		// Bot has all pieces in home section of board
    		// loop through home of bot and "weigh"
    		int totalWeight = 0;
    		for(int i = 1;i <= 6;i++) {
    			int weightForPipPosition = (7-i)*theBoard[opponent.getId()][i];
    			totalWeight += weightForPipPosition;
    		}

    		opponentBonus += (totalWeight * allPiecesInHomeOpponent_weight);

    	}

    	Double totalScore = 0.0;


        botScore = (blockBlotDifBot_weight*blockBlotDif_bots) + (botBlocksHome_weight* botHomeBlockCount) + (botBlockOpponentHome_weight*botBlockCountOpponentsHome)
        		+ (lengthOfBlockChainsBot_weight*lengthOfCurrentBlockChain_bots) + (botPipCountInJail_weight*botPiecesInJail) 
        		+ (botPipCountBearedOff_weight*botsPointsScored) + (botPipCountInHome_weight*botPipCountInHome_weight) + botBonus;
        
        botScore = (blockBlotDifBot_weight*blockBlotDif_opponents) + (opponentsBlocksHome_weight* opponentsHomeBlockCount) + (opponentBlockBotHome_weight*opponentsBlockCountBotsHome)
        		+ (lengthOfBlockChainsOpponent_weight*lengthOfCurrentBlockChain_opponents) + (opponentsPipCountInJail_weight*opponentsPiecesInJail) 
        		+ (opponentsPipCountBearedOff_weight*opponentsPointsScored) + (opponentsPipCountInHome_weight*opponentsPipCountInHome_weight) + opponentBonus;
        
    	totalScore += botScore + opponentScore + (pipCountDifference_weight*pipCountDifference);


    	return (totalScore);
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
        		//double weightAdjustment = ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);
        	    // POSITIVE
        		checkersInJailOpponent_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Positive

        	    pipCountDifference_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive	(for both bot and player) 
        	    botPipCountInHome_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
        	    botPipCountBearedOff_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
        	    opponentsPipCountInJail_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Positive
        	    lengthOfBlockChainsBot_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// Positive TODO: THIS IS JUST A TEST
        	    allPiecesInHomeBot_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// Positive	
        	    botBlockOpponentHome_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
        	    botBlocksHome_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
        	    blockBlotDifBot_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive


        	    
        	    
        	    
        	    // NEGATIVE
        	    checkersInJailBot_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Negative 
        	    opponentsPipCountInHome_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
        	    opponentsPipCountBearedOff_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
        	    botPipCountInJail_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// Negative
        	    lengthOfBlockChainsOpponent_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// negative TODO: THIS IS JUST A TEST
        	    allPiecesInHomeOpponent_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
        	    opponentBlockBotHome_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Negative
        	    opponentsBlocksHome_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
        	    blockBlotDifOpponent_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative

        	}
    	}
    }

    public void botWins() {
    	if(inTrainingMode) {
    		// Adjust the weight by small amount as described in document

    		//double weightAdjustment = ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);
    		//add a small random amount (could be slightly negative) to the
    		//positive weights and subtract a small amount from the negative weights (could be slightly negative
    	    
    	    // POSITIVE
    		checkersInJailOpponent_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Positive

    	    pipCountDifference_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive	(for both bot and player) 
    	    botPipCountInHome_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
    	    botPipCountBearedOff_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
    	    opponentsPipCountInJail_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Positive
    	    lengthOfBlockChainsBot_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// Positive TODO: THIS IS JUST A TEST
    	    allPiecesInHomeBot_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// Positive	
    	    botBlockOpponentHome_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
    	    botBlocksHome_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive
    	    blockBlotDifBot_weight += ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Positive


    	    
    	    
    	    
    	    // NEGATIVE
    	    checkersInJailBot_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Negative 
    	    opponentsPipCountInHome_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
    	    opponentsPipCountBearedOff_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
    	    botPipCountInJail_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// Negative
    	    lengthOfBlockChainsOpponent_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);			// negative TODO: THIS IS JUST A TEST
    	    allPiecesInHomeOpponent_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
    	    opponentBlockBotHome_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);		// Negative
    	    opponentsBlocksHome_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative
    	    blockBlotDifOpponent_weight -= ThreadLocalRandom.current().nextDouble(weightAdjustment_Min, weightAdjustment_Max + 0.0001);	// Negative

    		
    	}
    }

    public void swapWeightsWithOtherPlayer(double[] botWeights) {
    	
	    // POSITIVE
		checkersInJailOpponent_weight = botWeights[0];

	    pipCountDifference_weight = botWeights[1]; 
	    botPipCountInHome_weight = botWeights[2];
	    botPipCountBearedOff_weight = botWeights[3];
	    opponentsPipCountInJail_weight = botWeights[4];
	    lengthOfBlockChainsBot_weight = botWeights[5];
	    allPiecesInHomeBot_weight = botWeights[6];	
	    botBlockOpponentHome_weight = botWeights[7];
	    botBlocksHome_weight = botWeights[8];
	    blockBlotDifBot_weight = botWeights[9];


	    
	    
    	
    	// NEGATIVE
	    checkersInJailBot_weight = botWeights[10]; 
	    opponentsPipCountInHome_weight = botWeights[11];
	    opponentsPipCountBearedOff_weight = botWeights[12];
	    botPipCountInJail_weight = botWeights[13];
	    lengthOfBlockChainsOpponent_weight = botWeights[14];
	    allPiecesInHomeOpponent_weight = botWeights[15];
	    opponentBlockBotHome_weight = botWeights[16];
	    opponentsBlocksHome_weight = botWeights[17];
	    blockBlotDifOpponent_weight = botWeights[18];


    }
    
    

    // ONLY FOR TRAINING
    public double[] getWeights() {
    	double[] weights = {
    	    	checkersInJailOpponent_weight,pipCountDifference_weight,botPipCountInHome_weight,botPipCountBearedOff_weight,
    	    	opponentsPipCountInJail_weight,lengthOfBlockChainsBot_weight,allPiecesInHomeBot_weight,botBlockOpponentHome_weight,
    	    	botBlocksHome_weight,blockBlotDifBot_weight,checkersInJailBot_weight,opponentsPipCountInHome_weight,
    	    	opponentsPipCountBearedOff_weight,botPipCountInJail_weight,lengthOfBlockChainsOpponent_weight,
    	    	allPiecesInHomeOpponent_weight,opponentBlockBotHome_weight,opponentsBlocksHome_weight,blockBlotDifOpponent_weight};
    	return weights;
    }

    private void swapBotsWeightsWithOpponentBot(BotAPI opponentBot) {
    	double[] bot0_weights = this.getWeights();
    	double[] bot1_weights = opponentBot.getWeights();

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
        	double[] weights = this.getWeights();
			String output = "";
			for (double weight : weights) {
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
        	double[] weights = new double[AMOUNTOFWEIGHTS];
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

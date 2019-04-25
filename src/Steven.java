import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class Steven implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects
	
	// private final boolean TRAINING = true;
	private static final String WEIGHT_FILE = "Steven_WeightsForScoring.txt";
	
    public static final int BAR = 25;           	// Index of the BAR
    public static final int BEAR_OFF = 0;      		// Index of the BEAR OFF
    public static final int NUM_PIPS = 24;      	// Total number of pips on this board, EXECLUDING BAR and BEAR OFF
    
    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    private BotAPI opponentBot;
    
    // ----- ML Variables -----
    private double referenceScoreForBoard;
    private double referenceScoreForBot;
    private double referenceScoreForOpponent;

    // variables to keep track of so can adjust the weight by machine learning method
    private int botLosesInARow = 0;
    private final boolean TRAINING = true;
    
    // Weight adjustment variables
    private double weightAdjustment = 0.005;
    private double botWeight = 0;
    private double opponentWeight = 0;
    // ----- END OF ML Variables -----
    
    private double botScore = 0.0;
    private double opponentScore = 0.0;
    
    Steven(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
        
        referenceScoreForBoard = this.getScoreForBoard(board.get());
        referenceScoreForBot = botScore;
        referenceScoreForOpponent = opponentScore;
        referenceScoreForBoard = referenceScoreForBoard * 2;
    }

    /**
     * return the bot name (must match the class name)
     */
    public String getName() {
        return "Steven"; 
    }
    
    public static String getTheName() {
        return "Steven"; 
    }
    
    /**
     * Method to be defined as the decision-maker of offering a double challenge from this bot
     * @return	String, "double" if the condition passed, else "no"
     */
    public String initDouble() {
    	
    	if(botScore > opponentScore * 10)
    		return "double";
    	return "no";
    }
    
    /**
     * Method to get a move command 
     */
    public String getCommand(Plays possiblePlays) {
        
    	// Add your code here
    
    	// Get score for current board(without any moves)
    	double currentHighestScore = 0;
    	Play playWithHighestScore;
    	
    	int[][] originalBoard = board.get();
    	
    	boolean firstScoreGotten = false;
    	int currentPlayPointer = -1;
    	// Now calculate score for each play
    	for (Play currentPlay : possiblePlays) {
			
    		// Get the board after the move
    		int[][] shadowBoard_AfterMove = getBoardAfterPlay(originalBoard.clone(), currentPlay, me.getId());
    		
    		// Get the new boards score
    		double scoreOfBoardAfterMove = getScoreForBoard(shadowBoard_AfterMove);
    		
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
    
    // ----- Helper functions for getCommand -----
    
//    private boolean getIfPointIsInPlayersHome(int point) {
//    	if(point > 0 && point <= 6)
//    		return true;
//    	return false;
//    }
//    
//    private boolean getIfPointIsInPlayersEnemysHome(int point) {
//    	
//    	if(point > 18  && point <= 24)
//    		return true;
//    	return false;
//    }
    
    
    // End of helpers for getCommand

    /**
     * TODO
     */
    public String getDoubleDecision() {
    	
    	// Match Stage : 3 kind of stages -> Normal, Both Players 2 points from winning, Post Crawford
    	
    	// Stage 1 : Both Players 2 points from winning 
    	if(me.getScore() - 2 == match.getMatchPoint() && me.getScore() - 2 == match.getMatchPoint()) {
    		if(getScoreForBoard(board.get()) >= 50 && getScoreForBoard(board.get()) <= 75)
    			return "yes";
    		else if(getScoreForBoard(board.get()) >= 75)
    			return "no";
    		else
    			return "yes";
    		
    	} // Normal Stage
    	else {
    		
    		if(getScoreForBoard(board.get()) <= 75)
    			return "yes";
    		else
    			return "no";
    	}
    }
    
    public double getScoreForBoard(int[][] theBoard) {
        	
        double boardPoint = 0;
        int currentPlayerID = me.getId();
        	
       	// Counting checkers on board
        for(int pipPointer = 24 ; pipPointer >= 1 ; pipPointer--) {
        		
       		boardPoint +=  (24 - pipPointer) * board.getNumCheckers(currentPlayerID, pipPointer);
        }    	
        	
        // Bear-off point will be the highest : 24 points per borne-off checker
        if(board.hasCheckerOff(currentPlayerID))
        	boardPoint += 24 * board.getNumbCheckersAtPosition(0);
        	
        // Else if the current bot has at least 1 checker(s) in bar : Deduction of score
        else if(board.getNumbCheckersAtPosition(25) > 0)
        	boardPoint -= 24 * board.getNumCheckers(currentPlayerID, 25);
        	
        return (int) boardPoint * 10 / 36;	// return the value in percentage        
    }
    
    
    // For training
    
    
    public void botLoses() {
    	
    	if(TRAINING) {
    		botLosesInARow++;
        	if(botLosesInARow == 3) {
        		// Exchange the weights between the bots
        		swapBotsWeightsWithOpponentBot(opponentBot);
        		
        		// End of changing the weights between bots
        		botLosesInARow = 0;	// Reset
        	}
        	else {
        		// Adjust the weights by small amount as described in document
        		double weightAdjustment = ThreadLocalRandom.current().nextDouble(this.weightAdjustment - 0.001, this.weightAdjustment + 0.001);
        		this.weightAdjustment = weightAdjustment;
        		
        		// positive weights
        		botWeight += weightAdjustment;
        		
        		// Negative weights
        		opponentWeight -= weightAdjustment;
        	}
    	}
    }
    
    public void botWins() {
    	
    	if(TRAINING) {
    		// Adjust the weight by small amount as described in document
    		
    		double weightAdjustment = ThreadLocalRandom.current().nextDouble(this.weightAdjustment - 0.001, this.weightAdjustment + 0.001);
    		this.weightAdjustment = weightAdjustment;
    		//add a small random amount (could be slightly negative) to the 
    		//positive weights and subtract a small amount from the negative weights (could be slightly negative
    		
    		// positive weights
    		botWeight -= weightAdjustment;
    		
    		// Negative weights
    		opponentWeight += weightAdjustment;
    		
    	}
    }
    
    public void swapWeightsWithOtherPlayer(Double[] botWeights) {
		
    	double temp = botWeight;
    	botWeight = opponentWeight;
    	opponentWeight = temp;
    }
    
    // ONLY FOR TRAINING
    public Double[] getWeights() {
    	Double[] weights = {botWeight, opponentWeight};
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
        if (playersMove.getToPip() < BAR && playersMove.getToPip() > BEAR_OFF &&
        		shadowBoard[opponent.getId()][calculateOpposingPip(playersMove.getToPip())] == 1) {
        	shadowBoard[opponent.getId()][calculateOpposingPip(playersMove.getToPip())]--;
        	shadowBoard[opponent.getId()][BAR]++;
        }
		
		return shadowBoard;
	}
    
    private int calculateOpposingPip(int pip) {
        return NUM_PIPS - pip + 1;
    }

	@Override
	public void saveWeights() {
		// TODO Auto-generated method stub
		// Write weights to file
        try {
        	//OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(new File(WEIGHT_FILE)));
        	FileWriter out = new FileWriter(WEIGHT_FILE);
			Double[] weights = this.getWeights();
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

	@Override
	public void retrieveWeights() {
		// retrieve the weights
        try {
        	BufferedReader bf1 = new BufferedReader(new FileReader(WEIGHT_FILE));
        	
        	Double[] weights = new Double[2];
			for(int i = 0; i < 2;i++) {
				weights[i] = Double.parseDouble(bf1.readLine());
				// weights[i] = Integer.parseInt(bf1.readLine());
			}
			swapWeightsWithOtherPlayer(weights);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    // SEEING THE BOARD
    
}

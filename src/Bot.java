import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of a general bot class
 * TODO Will be done after the module ends
 * @author Ee En Goh 17202691
 *
 */
public class Bot implements BotAPI {

	private Bot opponentBot;
	private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
    private String name;
    private double boardScore;
    
    // ----- ML Variables -----
    /** Default weight file name */
    private final boolean TRAINING = true;
    private String WEIGHT_FILE = "Bot_Weight_File.txt";  
    private double weightAdjustment = 0.005;
    private double weight = 0.0;
    private double doubleDecisionWeight = 0.0;
    private int winStreak = 0;
    private int lossStreak = 0;
    private double referenceScore;
    private double referenceBoardScore;
    // ----- End of ML Variables -----    
    
    Bot(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info){
    	
    	this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
        this.boardScore = 0.0;
        
        referenceBoardScore = this.getBoardScore(board.get());
        referenceScore = this.boardScore;
    }
    
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	/**
	 * Method to decide if the current bot decides to offer a doubling cube challenge
	 * @return	String "double" if offering, else "no"
	 */
	public String initDouble() {
		
		double percentageChanceOfSuccess = percentageChanceOfsuccess(getBoardScore(board.get()));
		
		// Stage 1 : Both Players 2 points from winning
    	if(me.getScore() - 2 == match.getMatchPoint() && me.getScore() - 2 == match.getMatchPoint()) {

    		// If the current bot wants to offer a doubling challenge to the opponent
    		if(percentageChanceOfSuccess >= 50) {
    			return "double";
    		}
    		else
    			return "no";
    	} 
    	// Stage 2 : Normal Stage
    	else {
    		// Bot is not being asked to double, and instead is going to choose for himself
    		if(percentageChanceOfSuccess < 66)
    			return "no";
    		else if(percentageChanceOfSuccess >= 66 && percentageChanceOfSuccess <= 80) 
    			return "double";
    		// > 80% with significant gammon changes ??
    		else
    			return "no";
    	}
	}
	
	@Override
	public String getDoubleDecision() {
		
		double successPercent = percentageChanceOfsuccess(getBoardScore(board.get()));
		
		// Stage 1 : Both Players 2 points from winning
    	if(me.getScore() - 2 == match.getMatchPoint() && me.getScore() - 2 == match.getMatchPoint()) {
    		
    		// The current bot is being asked accept the doubling challenge offered by opponent
    		if(successPercent >= 25) { // Opponent has the winning percentage lower than 75%
    			return "yes";
    		}
    		else
    			return "no";

    	} // Stage 2 : Normal Stage
    	
    	else {
    		
    		if(successPercent > 34)
    			return "yes";
    		else if(successPercent <= 34) 
    			return "no";
    	}
    	
    	return "yes";
	}
	
	private double percentageChanceOfsuccess(double currentScore) {
    	// Get the percentage in respect to references (board, bot and opponent)
    	double remainder_boardScore = currentScore - referenceBoardScore;
    	double remainderbotScore = boardScore - referenceScore;
    	double remainder_opponentScore = opponentBot.boardScore - opponentBot.referenceScore;
    	
    	double percentageChangeForBoardScore = (remainder_boardScore/referenceBoardScore) * 100;
    	double percentageChangeForBotScore = (remainderbotScore/referenceScore) * 100;
    	double percentageChangeForOpponentScore = (remainder_opponentScore/opponentBot.boardScore) * 100;
    	
    	double averagePercentageChange = (percentageChangeForBoardScore + percentageChangeForBotScore + percentageChangeForOpponentScore)/3;
    	
    	return averagePercentageChange;
    }
	
	public double getBoardScore(int [][]plays) {
		
		
		return this.boardScore;
	}
	
	@Override
	public String getCommand(Plays possiblePlays) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getWeights() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void swapWeightsWithOtherPlayer() {
		double botWeight = this.getWeight();
		this.setWeight(opponentBot.getWeight());
		opponentBot.setWeight(botWeight);
	}
	
	@Override
	public void saveWeights() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveWeights() {
		
		// retrieve the weights
        try {
        	BufferedReader bf1 = new BufferedReader(new FileReader(WEIGHT_FILE));
        	Double weights = Double.parseDouble(bf1.readLine());
        	// Double[] weights = new Double[AMOUNTOFWEIGHTS];
			// for(int i = 0; i < AMOUNTOFWEIGHTS;i++) {
				// weights[i] = Double.parseDouble(bf1.readLine());
			// }

			// swapWeightsWithOtherPlayer(weights);
			bf1.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void botWins() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void botLoses() {
    	if(TRAINING) {
    		this.winStreak = 0;
    		this.lossStreak++;
    		opponentBot.winStreak++;
    		
        	if(this.lossStreak == 5) {

        		// End of changing the weights between bots
        		this.swapWeightsWithOtherPlayer();			// Swap the weight with other player / bot
        		this.resetLossStreak();
        	}
        	else {
        		// Adjust the weights by small amount as described in document
        		double weightAdjustment = ThreadLocalRandom.current().nextDouble(this.weightAdjustment, this.weightAdjustment + 0.001);
        		
        		// positive weights
        		this.weight += weightAdjustment;
        	}
    	}
		
	}

	public int getWinStreak() {
		return this.winStreak;
	}
	
	public void setWinStreak() {
		this.winStreak++;
	}
	
	public int getLossStreak() {
		return this.lossStreak;
	}
	
	public void setLossStreak() {
		this.lossStreak++;
	}
	
	public void resetLossStreak() {
		this.lossStreak = 0;
	}
	
	public Bot getOpponentBot() {
		return opponentBot;
	}

	public void setOpponentBot(Bot opponentBot) {
		this.opponentBot = opponentBot;
	}

	public void swapWeightsWithOtherPlayer(double[] botWeights) {
		// TODO Auto-generated method stub
		
	}

}

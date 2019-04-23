
public class Match implements MatchAPI{

	private Game game;
    private Cube cube;
    private Players players;
    private int matchPoint;
    private int matchLength;
    private int gamePlayed;
    
    /** A game without doubling cube, when the leading player comes within 1 point in order to win the game */
    private boolean crawford, crawfordDone;

    Match(Game game, Cube cube, Players players) {
        this.game = game;
        this.cube = cube;
        this.players = players;
        matchLength = 1;
        gamePlayed = 0;
        matchPoint = 0;
        crawford = false;
        crawfordDone = false;
    }

	@Override
	public int getLength() {
		return matchLength;
	}

    public void setLength() {
        this.matchLength++;
    }
    
    public int getMatchPoint() {
    	return matchPoint;
    }
    
    public void setMatchPoint(int point) {
    	this.matchPoint = point;
    }
    
    public boolean isOver() {
    	return (players.get(0).getScore() >= matchPoint || players.get(1).getScore() >= matchPoint || game.getResignedByCommand());
    }
    
    /**
     * Method to determine the winner of current match (Finish the current game)
     * @return	Player object of the winner, null if it is a deuce
     */
    public Player getWinner() {
    	
    	if(game.getResignedByCommand())
    		return players.getEnemy();
    	else if(players.get(0).getScore() > players.get(1).getScore())
    		return players.get(0);
    	else if(players.get(0).getScore() < players.get(1).getScore())
    		return players.get(1);
    	else if(players.get(0).getScore() == players.get(1).getScore())	// Deuce
    		return null;	
    	else {
    		System.out.println("Some winning cases not considered");
    		return null;
    	}
    }
    
    /**
     * Method to update the match scores earned by the winner
     * @param points	Winning score
     */
    public void updateScores() {
    	
        Player winner = game.getWinner();
        winner.addScore(game.getPoints());	// Update winner's match score from the recent match
        
        if (!crawfordDone && (players.get(0).getScore() == matchPoint - 1 || players.get(1).getScore() == matchPoint - 1 )) {
            crawford = true;
            crawfordDone = true;
        } else {
            crawford = false;
        }
    }
    
    /**
     * Method that check if the current game enables doubling dice<br>
     * - Check if it is a crawford game or the current doubling value is bigger than the score gap of the leading player and the max score 
     * @param player	Player for the current turn
     * @return			boolean value
     */
	@Override
	public boolean canDouble(Player player) {
		if(crawford || cube.getValue() < matchPoint - player.getScore())
			return true;
		return false;
	}
	
	public void reset() {
		this.matchLength = 0;
		this.matchPoint = 0;
		players.reset();
		crawford = false;
		crawfordDone = false;
	}
}

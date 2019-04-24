/**
 * Definition of a game
 * @author Ee En Goh 17202691
 *
 */
public class Game {

	private Players players;
    private Cube cube;
    private Board board;
    private boolean resignedByCommand;
    private boolean resignedByDouble;
    private Player winner;
    private int length;
    
    Game(Board board, Cube cube, Players players) {
        this.players = players;
        this.cube = cube;
        this.board = board;
        this.length = 0;
        resignedByDouble = false;
        resignedByCommand = false;
    }
    
    /**
     * Method to get if a game is over, in 2 cases :<br>
     * 1 - One of the players surrender<br> 
     * 2 - There is a winner who got all his/her checkers off board<br>
     * @return	true if it is, else false
     */
    public boolean isOver() {
    	
    	boolean gameOver = false;
    	if(resigned())
    		gameOver = true;
    	else if(board.allCheckersOff(0)||board.allCheckersOff(1))
    		gameOver = true;
    	return gameOver;
    }
    
    public int getGameLength() {
    	return this.length;
    }
    
    public void setGameLength() {
    	this.length++;
    }
    
    /**
     * Method that determine a winner when the game is finished<br>
     * @return The instance of Player of the winner
     */
    public Player getWinner() {
    	
    	if(!resigned()) {
    		if(board.allCheckersOff(0))
    			winner = players.get(0);
    		else if(board.allCheckersOff(1))
    			winner = players.get(1);
    	}
    	
    	return winner;
    }
    
    private Player getLoser() {
    	
        Player loser = players.get(0);
        if (resigned()) {
            players.getEnemy(winner);
        }else{
            if (board.lastCheckerInInnerBoard(0)) {
                loser = players.get(1);
            } else if (board.lastCheckerInInnerBoard(1)) {
                loser = players.get(0);
            }
        }
        return loser;
    }
    
    public boolean isSingle() {
        return board.hasCheckerOff(getLoser().getId());
    }

    public boolean isGammon() {
        return !board.hasCheckerOff(getLoser().getId()) && !board.lastCheckerInOpponentsInnerBoard(getLoser().getId());
    }

    public boolean isBackgammon() {
        return !board.hasCheckerOff(getLoser().getId()) && board.lastCheckerInOpponentsInnerBoard(getLoser().getId());
    }
    
    public boolean getResignedByCommand() {
    	return resignedByCommand;
    }

    /**
     * Method to process the game info where there are player want to quit this program<br>
     * ** Winner has been defined here
     * @param player	Resigned player
     */
    public void setResignedByCommand(Player player) {
        this.resignedByCommand = true;
        winner = players.getEnemy(player);
    }
    
    public boolean getResignedByDouble() {
    	return resignedByDouble;
    }
    
    /**
     * Method to process the game info where the player end a game by rejecting a doubling cube<br>
     * ** Winner has been defined here
     * @param player	Resigned player
     */
    public void setResignedByDouble(Player player) {
        this.resignedByDouble = true;
        winner = players.getEnemy(player);
    }
    
    public boolean resigned() {
    	return resignedByCommand || resignedByDouble;
    }
    
    /**
     * Method to get the total points that the winner will earned from this game
     * @return	Total points for winner in current game
     */
    public int getPoints() {
    	
    	int points = 0;
    	if(getResignedByDouble())
    		points = cube.getValue();
    	else {
    		if(isSingle())
    			points = 1;
    		else if(isGammon())
    			points = 2;
    		else if(isBackgammon())
    			points = 3;
    		else
    			System.out.println("Something is wrong in point calculation");	
    		points *= cube.getValue();
    	}
    	return points;
    }
    
    /**
     * Reset the variables modified in a game
     */
    public void reset() {
    	resignedByCommand = false;
    	resignedByDouble = false;
    	length = 0;
    	winner = null;
    	cube.reset();
    	board.reset();
    }
    
}

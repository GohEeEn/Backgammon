import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Players class that created to groups two Players as a unit
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class Players implements Iterable<Player>, Iterator<Player> {

    public static int NUM_PLAYERS = 2;

    private ArrayList<Player> players;
    private int currentPlayer;
    private Iterator<Player> iterator;

    Players() {
        players = new ArrayList<Player>();
        players.add(new Player(0,"RED", new Color(255,51,51)));
        players.add(new Player(1,"GREEN",Color.GREEN));
        currentPlayer = 0;
    }

    Players(Players players) {
        this.players = new ArrayList<Player>();
        for (Player player : players) {
            this.players.add(new Player(player));
        }
        currentPlayer = 0;
    }

    public void setCurrentAccordingToDieRoll() {
        if (players.get(0).getDice().getDie() > players.get(1).getDice().getDie()) {
            currentPlayer = 0;
        } else {
            currentPlayer = 1;
        }
    }

    /**
     * Method to return the current player object 
     * @return Current player
     */
    public Player getCurrent() {
        return players.get(currentPlayer);
    }
    
    /**
     * @return the opponent's player ID
     */
    public Player getEnemy() {
    	if(currentPlayer == 0) {
    		// enemy is player 2
    		return players.get(1);
    	}
    	else {
    		// enemy is player 1
    		return players.get(0);
    	}
    }

    public Player getEnemy(Player current) {
    	
    	if(current.getId() == 0)
    		return players.get(1);
    	else
    		return players.get(0);
    }
    
    public void advanceCurrentPlayer() {
    	currentPlayer++ ;
        currentPlayer %= NUM_PLAYERS ;
    }
    
    /**
     * Method to get specific player object 
     * @param id Given player ID
     * @return 0 for player 1, 1 for player 2
     */
    public Player get(int id) {
        return players.get(id);
    }

    public boolean isEqualDice() {
        return players.get(0).getDice().getDie() == players.get(1).getDice().getDie();
    }

    /**
     * Reset the Players object to restart a new match 
     */
    public void reset() {
    	for(int i = 0 ; i < Backgammon.NUM_PLAYERS ; i++)
    		players.get(i).reset();
    }

    
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Player next() {
        return iterator.next();
    }
    
    public Iterator<Player> iterator() {
        iterator = players.iterator();
        return iterator;
    }
}

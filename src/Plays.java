import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
import java.util.Iterator;

/**
 * Plays class is a list of legal plays that a player can make in the current turn
 * @author Ee En Goh 17202691
 */
public class Plays implements Iterable<Play>, Iterator<Play> {

    ArrayList<Play> plays;
    private Iterator<Play> iterator;

    Plays() {
        plays = new ArrayList<Play>();
    }

    Plays(Plays plays) {
        this.plays = new ArrayList<Play>();
        for (Play play : plays) {
            this.plays.add(play);
        }
    }

    public void add(Play play) {
        plays.add(play);
    }

    public void add(Plays plays) {
        for (Play play : plays) {
            this.plays.add(play);
        }
    }

    /**
     * @return The total number of valid play in the current game turn
     */
    public int number() {
        return plays.size();
    }

    /**
     * Method to get a specific checker move on the current move list
     * @param index The move index of target move on the current move list
     * @return The checker move selected by the given move index
     */
    public Play get(int index) {
        return plays.get(index);
    }

    public void prependAll(Move move) {
        for (Play play : plays) {
            play.prepend(move);
        }
    }

    public boolean contains(Play query) {
        for (Play play : plays) {
            if (play.matches(query)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove plays that give the equivalent checker move which have exist in list in an inverse way
     */
    public void removeDuplicatePlays() {
    	
        Plays duplicatePlays = new Plays(this);
        plays.clear(); // Clear the whole list
        
        for (Play play : duplicatePlays) {
            if (!this.contains(play)) {
                this.add(play);
            }
        }
    }

    /**
     * Remove plays with too few moves, which is not equal to the total number of dice roll value
     */
    public void removeIncompletePlays() {
    	
        int maxNumberOfMoves = 0;
        
        for (Play play : plays) {
            if (play.numberOfMoves() > maxNumberOfMoves) {
                maxNumberOfMoves = play.numberOfMoves();
            }
        }
        
        Plays duplicatePlays = new Plays(this);
        plays.clear();
        
        for (Play play : duplicatePlays) {
            if (play.numberOfMoves() == maxNumberOfMoves) {
                plays.add(play);
            }
        }
        // Remove single die plays that don't play the largest die
        if (maxNumberOfMoves == 1) {
            int maxMove = 0;
            for (Play play : plays) {
                if (play.getMove(0).getPipDifference() > maxMove) {
                    maxMove = play.getMove(0).getPipDifference();
                }
            }
            duplicatePlays = new Plays(this);
            plays.clear();
            for (Play play : duplicatePlays) {
                if (play.getMove(0).getPipDifference() == maxMove) {
                    plays.add(play);
                }
            }
        }
    }
    
    public boolean hasNext() {
        return iterator.hasNext();
    }

    public Play next() {
        return iterator.next();
    }

    public Iterator<Play> iterator() {
        iterator = plays.iterator();
        return iterator;
    }

}

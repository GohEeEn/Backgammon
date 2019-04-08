/**
 * Move class that holds the details of single checker movement from one pip to another.
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class Move {
	
	/** The pip index of where the checker will move from in the current move */
    private int fromPip;
    
    /** The pip index of where the checker will move to in the current move */
    private int toPip;
    
    /** Boolean value to check if the current move is a hit move */
    private boolean hit;

    Move() {
        fromPip = 0;
        toPip = 0;
        hit = false;
    }

    Move(int fromPip, int toPip, boolean hit) {
        this.fromPip = fromPip;
        this.toPip = toPip;
        this.hit = hit;
    }

    public int getFromPip() {
        return fromPip;
    }

    public int getToPip() {
        return toPip;
    }

    public int getPipDifference() {
        return fromPip - toPip;
    }

    public boolean isHit() {
        return hit;
    }

    /**
     * Boolean method to check if the current move is according to the move query given
     * @param query
     * @return true if both the moveFrom and moveTo index of this move and given move query are the same, else false
     */
    public boolean equals(Move query) {
        return this.fromPip == query.fromPip && this.toPip == query.toPip;
    }

    public String toString() {
        
    	String fromPipText, toPipText, hitText;
        
    	// After hit move case : opponent's checker(s) that got hit must be moved out from BAR
        if (fromPip == Board.BAR) {
            fromPipText = "Bar";
        }else{
            fromPipText = Integer.toString(fromPip);
        }
        
        // Bear-off case
        if (toPip == Board.BEAR_OFF) {
            toPipText = "Off";
        } else {
            toPipText = Integer.toString(toPip);
        }
        
        // Hit move case (*)
        if (hit) {
            hitText = "*";
        } else {
            hitText = "";
        }
        
        return fromPipText + "-" + toPipText + hitText;
    }

}

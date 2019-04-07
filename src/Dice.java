/**
 * Dice holds the details for a one die or two numbers roll
 * @author Ee En Goh 17202691
 *
 */
class Dice {

	/** int array to store the dice roll value */
    private int[] numbers;
    
    /** Game Starter Dice Roll */
    private boolean oneDieRoll;

    Dice() {
        numbers = new int[]{1, 1};
        oneDieRoll = false;
    }
    
    Dice(Dice dice) {
        numbers = new int[]{dice.numbers[0],dice.numbers[1]};
        oneDieRoll = dice.oneDieRoll;
    }
    
    /**
     * Constructor for normal checker move : 2 dice rolls per player
     * @param firstDie
     * @param secondDie
     */
    Dice(int firstDie, int secondDie) {
        numbers = new int[]{firstDie, secondDie};
    }

    public void rollDie() {
        numbers[0] = 1 + (int)(Math.random()*6);
        oneDieRoll = true;
    }

    public void rollDice() {
    	
        numbers[0] = 1 + (int)(Math.random() * 6);
        numbers[1] = 1 + (int)(Math.random() * 6);
        
        oneDieRoll = false;
    }
    
    public int getDie() {
        return numbers[0];
    }

    public int getDie(int index) {
        return numbers[index];
    }

    public boolean isDouble() {
        return numbers[0] == numbers[1];
    }

    public String toString() {
        
    	String roll;
    	
        if (oneDieRoll) {
            roll = "[" + numbers[0] + "]";
            
        }else if(isDouble()) {
        	roll = "[" + numbers[0] + "," + numbers[1] + "] -> [" + numbers[0] + "," + numbers[1] + "," + numbers[0] + "," + numbers[1] + "]";
        	
        }
        else {
            roll = "[" + numbers[0] + "," + numbers[1] + "]";
            
        }
        return roll;
    }

}

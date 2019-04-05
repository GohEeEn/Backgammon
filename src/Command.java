/**
 * Command class that holds a user entered command and processes its syntax
 * @author Ee En Goh 17202691
 */
public class Command {

    /** The given command by the current player on the Command Panel */
	private String input;
    
    /**
     * Boolean value that shows the characteristics of the given command :<br>
     * - valid - If the command given is valid<br>
     * - move  - If the command given is to make a move<br>
     * - cheat - If the command given is to activate the cheat move<br>
     * - quit  - If the command given is to quit the program<br>
     */
    private boolean valid, move, quit, cheat;
    
    /** */
    private Play play;

    Command() {
        input = "";
        valid = true;
        move = false;
        cheat = false;
        quit = false;
    }
    
    /**
     * Constructor that in charge of differentiate the given command's purpose
     * @param input	Given command on the command panel
     * @param possiblePlays Move list given
     */
    Command(String input, Plays possiblePlays) {
    	
        // regex examples: "[a-f]", "[a-z]|a[a-g]", "[a-z]|a[a-z]|b[a-n]"
        int numberOfFirstLetters = (possiblePlays.number() - 1) / 26;
        char finalLetterLimit = (char) ((possiblePlays.number() - 1) % 26 + (int) 'a');
        
        String regex = "";
        char firstChar = 'a';
        
        for (int i=0; i<numberOfFirstLetters; i++) {
            regex = regex + "[a-z]|" + firstChar;
            firstChar++;
        }
        regex = regex + "[a-" + finalLetterLimit + "]";
        this.input = input;
        String text = input.toLowerCase().trim();
        
        // Case if quit program command given
        if (text.equals("quit")) {
            valid = true;
            move = false;
            cheat = false;
            quit = true;
            
        } // Case if move command given
        else if (text.matches(regex)) {
        	
            int option = 0;
            if (text.length()==1) {
                option = (int) text.charAt(0) - (int) 'a';
            } else if (text.length()==2) {
                option = ((int) text.charAt(0) - (int) 'a' + 1) * 26 + (int) text.charAt(1) - (int) 'a';
            }
            play = possiblePlays.get(option);
            valid = true;
            move = true;
            cheat = false;
            
        }else if (text.equals("cheat")) {
            valid = true;
            move = false;
            cheat = true;
        } else {
            valid = false;
        }
    }

    public Play getPlay() {
        return play;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isMove() {
        return move;
    }
    
    public void quit() {
    	quit = true;
    }

    public boolean isQuit() {
        return quit;
    }

    public boolean isCheat() {
        return cheat;
    }

    public String toString() {
        return input;
    }
}

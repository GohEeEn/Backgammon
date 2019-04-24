import java.awt.BorderLayout;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

/**
 * UI class is the top level interface to the user interface
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class UI { 

    private static final int FRAME_WIDTH = 1120;
    private static final int FRAME_HEIGHT = 600;
    
    
    //private final BoardPanel 
    private final BoardPanel boardPanel;
    private final InfoPanel infoPanel;
    private final CommandPanel commandPanel;

    UI (Board board, Players players, Cube cube, Match match, BotAPI[] bots) {
        infoPanel = new InfoPanel();
        commandPanel = new CommandPanel();
        JFrame frame = new JFrame();
        boardPanel = new BoardPanel(board,players,cube,match);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Backgammon");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.LINE_END);
        frame.add(commandPanel, BorderLayout.PAGE_END);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    public InfoPanel getInfoPanel() {
    	return infoPanel;
    }

    public String getString() {
        return commandPanel.getString();
    }

    // ----- PROMPTS DISPLAY METHODS -----
    public void display() {
        boardPanel.refresh();
    }

    public void clearInfo() {
    	infoPanel.clearText();
    }
    
    /**
     * Method to display any given string on the info panel
     * @param string
     */
    public void displayString(String string) {
        infoPanel.addText(" " + string);
    }
  
    public void displayStartOfGame() {
        displayString("\t>> Welcome to Backgammon! <<");
    }

    public void promptPlayerName() {
        displayString("Enter a player name:");
    }
    
    public void promptPlayersForGamePoint() {
    	displayString("Enter the number of points are playing up to:");
    }
    
    public void promptPlayersNextGame() {
    	displayString("Do you wish to play next game? (yes/no)");
    }
    
    public void promptRestartNewMatch() {
    	displayString("Do you wish to play next match? (yes/no)");
    }
    
    public void promptPlayerToDouble() {
    	displayString("Enable double play with command 'double', else press 'Enter' key");
    }
    
    public void promptPlayerToRedouble() {
    	displayString("Enable redouble play with command 'double', else press 'Enter' key");
    }
    
    public void promptOpponentToAcceptDouble(String opponent) {
    	displayString("Player " + opponent + " , do you want to accept the double? (yes/no)");
    }
    
    public void print_doubleTheScore() {
    	displayString("The score has been doubled\n");
    }
    
    public void displayPlayerColor(Player player) {
        displayString(player + " uses " + player.getColorName() + " checkers.");
    }

    public void displayRoll(Player player) {
        displayString(player + " (" + player.getColorName() + ") rolls " + player.getDice());
    }

    public void displayDiceEqual() {
        displayString("Equal. Roll again");
    }

    public void displayDiceWinner(Player player) {
        displayString(player + " wins the roll and goes first.\n");
    }
    
    public void display_CurrentMatchScores(Players players) {
    	displayString("Player 1 [" + players.get(0).toString() + "] currently has score " + players.get(0).getScore());
    	displayString("Player 2 [" + players.get(1).toString() + "] currently has score " + players.get(1).getScore());
    	displayString("");
    }
    
    public void promptCommand(Player player) {
        displayString(player + " [" + player.getColorName() + "] Enter your move or quit:");
    }
    
    /**
     * Method to notify the current player it is his/her game turn
     * @param playerName	Current player
     */
    public void print_CurrentPlayer(String playerName) {
    	displayString("Player " +  playerName + ", it is your turn.");
    }
    // ----- END OF PROMPTS DISPLAY METHODS -----
    
    // ----- MOVE METHODS -----
    public Command getCommand(Plays possiblePlays) {
        
    	Command command;
        
        do {
            String commandString = commandPanel.getString();
            command = new Command(commandString, possiblePlays);
            
            if (!command.isValid()) {
                displayString("Error: Command not valid. Try again");
            }
            
        } while (!command.isValid());
        
        return command;
    }

    /**
     * Method to display the valid checker move list
     * @param player	The current player
     * @param plays		Valid checker move of the current player
     */
    public void displayPlays(Player player, Plays plays) {
        
    	displayString(player + " (" + player.getColorName() + ") has available moves : ");
        
    	int index = 0; // Corresponding numeric move index 
    	
        for (Play play : plays) {
        	
            String code ; // Move index [2 characters is enough (26 * 26 = 476 moves)]
            
            // [A - Z] : Move Index for the first 26 moves
            if (index < 26) {
                code = "" + (char) (index % 26 + (int) 'A');
                
            }// [AA - ZZ] - [27 - 476]
            else{ 
                code = "" + (char) (index / 26 - 1 + (int) 'A') + (char) (index % 26 + (int) 'A');
            }
            displayString(code + ". " + play);
            
            index++;
        }
    }       
    
    // ----- END OF MOVE METHODS -----
    
    // ----- MOVE RESPOND MESSAGES -----
    
    public void displayNoMove(Player player) throws InterruptedException {
        displayString(player + " has no valid moves.");
        // TimeUnit.SECONDS.sleep(1);
    }

    public void displayForcedMove(Player player) throws InterruptedException {
        displayString(player + " has a forced move.");
        // TimeUnit.SECONDS.sleep(1);
    }
    
    // ----- END OF MOVE RESPOND MESSAGES -----
    
    // ----- ERROR RESPOND MESSAGES -----
    public void displayError_WrongScoreToWinEntered() {
    	displayString("Invalid Gamepoint : Please enter a number greater 0 and equal or less than the number of players pips\n");
    }
    
    public void displayError_WrongInputForNumberOfPointsPlayingTo() throws InterruptedException {
    	displayString("Wrong input for number of moves, please try again\n");
    	// TimeUnit.SECONDS.sleep(1);
    }
    
    public void displayError_incorrectEntry() throws InterruptedException {
    	displayString("Invalid Command : Please try again\n");
    	// TimeUnit.SECONDS.sleep(1);
    }
    
    public void displayError_WrongInputGivenForRepeatingOrEndingGame() throws InterruptedException {
    	displayString("Wrong input, please enter 'yes' or 'no', please try again\n");
    	// TimeUnit.SECONDS.sleep(1);
    }
    
    // ----- END OF ERROR RESPOND MESSAGES -----
    
    // ----- END GAME MESSAGES -----
    public void display_roundWinner(Player winner) {
    	displayString(winner.toString() + " WIN THIS GAME ! KEEP IT UP ! ");
    }
    
    /**
     * Method to display the winner of the whole game
     * @param player_id		Winner's player ID
     * @param playerName	Winner's name
     */
    public void display_matchWinner(String playerName) {
    	displayString("Player " +  playerName + " HAS WON THE MATCH! CONGRAT !!");
    }
    
    /**
     * Method to display the message when the game end by rejecting the double play
     * @param winnerName	
     * @param loserName
     * @param earnScore The match score the winner earns in the recent match	
     */
    public void print_rejectedDoubleTheScore(String winnerName , String loserName , int earnScore) {
    	displayString("\nPlayer " + loserName + " rejects the doubling challenge, so " + winnerName + 
    			" win and gain " + earnScore + " points in current game turn.");
    }
    
    public void display_PlayersWantNextGame() throws InterruptedException{
    	displayString("Next game will start in 1 second\n");
    	// TimeUnit.SECONDS.sleep(1);
    }
    
    public void display_PlayersWantQuit() {
    	displayString("Players want to QUIT PROGRAM");
    }
    
    public void display_PlayersWantQuitMatch() {
    	displayString("\n Players want to QUIT current match");
    }
    
    public void display_PlayersWantNextMatch() throws InterruptedException{
    	displayString("New Match will start in 3 second\n");
    	// TimeUnit.SECONDS.sleep(3);
    }
    
    public void display_newGame(){
    	displayString("=============== NEW GAME ===============");
    }
    
    public void display_endGame() throws InterruptedException {
    	displayString("=============== END GAME ===============\n");
    	// TimeUnit.SECONDS.sleep(1);
    }
    
    public void display_newMatch(){
    	displayString("=============== NEW MATCH ===============");
    }
    
    public void display_endMatch() throws InterruptedException {
    	displayString("=============== END MATCH ===============\n");
    	// TimeUnit.SECONDS.sleep(3);
    }
    
    public void display_endProgram() throws InterruptedException {
    	displayString("=============> END BACKGAMMON <=============\n");
    	// TimeUnit.SECONDS.sleep(3);
    }
    
    // ----- END OF END GAME MESSAGES -----
}
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

/**
 * UI class is the top level interface to the user interface
 * @author Ee En Goh 17202691
 *
 */
public class UI { 

    private static final int FRAME_WIDTH = 1250;
    private static final int FRAME_HEIGHT = 600;
    
    
    //private final BoardPanel 
    private final BoardPanel boardPanel;
    private final InfoPanel infoPanel;
    private final CommandPanel commandPanel;
    private final GameInformationPanel gameInfoPanel;

    UI (Board board, Players players) {
        infoPanel = new InfoPanel();
        commandPanel = new CommandPanel();
        JFrame frame = new JFrame();
        boardPanel = new BoardPanel(board,players);
        gameInfoPanel = new GameInformationPanel();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Backgammon");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(gameInfoPanel, BorderLayout.LINE_START);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.add(infoPanel, BorderLayout.LINE_END);
        frame.add(commandPanel, BorderLayout.PAGE_END);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public String getString() {
        return commandPanel.getString();
    }

    // ----- PROMPTS DISPLAY METHODS -----
    public void display() {
        boardPanel.refresh();
    }

    public void displayString(String string) {
        infoPanel.addText(" " + string);
    }
  
    public void displayStartOfGame() {
    	displayString("=============== NEW GAME ===============");
        displayString("\t>> Welcome to Backgammon! <<");
    }

    public void promptPlayerName() {
        displayString("Enter a player name:");
    }
    
    public void promptPlayersForGamePoint() {
    	displayString("Enter the number of points are playing up to:");
    }
    
    public void promptPlayersNextMatch() {
    	displayString("Do you wish to play next match? (yes/no)");
    }
    
    public void promptRestartNewGame() {
    	displayString("Do you wish to play next game? (yes/no)");
    }
    
    public void promptPlayerToDouble() {
    	displayString("Enable double play with command 'double', else press 'Enter' key");
    }
    
    public void promptPlayerToRedouble() {
    	displayString("Enable redouble play with command 'double', else press 'Enter' key");
    }
    
    /*
    public void promptOpponentIfAcceptDouble() {
    	displayString("Do you want to accept the double? (yes/no)");
    }
    */
    
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
        displayString(player + " wins the roll and goes first.");
    }
    
    public void display_CurrentPlayersScores(int player1_winCount, int player2_winCount) {
    	displayString("Player 1 has a score of: " + player1_winCount + ", and player 2 has a score of: " + player2_winCount);
    }

    public void displayGameWinner(Player player) {
        displayString(player + " WINS THE GAME!!!");
    }

    public void promptCommand(Player player) {
        displayString(player + " [" + player.getColorName() + "] Enter your move or quit:");
    }
    
    public void print_doubleTheScore() {
    	displayString("The score has been doubled");
    }
       
    public void print_CurrentPlayer(String playerName) {
    	displayString("Player " +  playerName + ", it is now your turn.");
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
        TimeUnit.SECONDS.sleep(1);
    }

    public void displayForcedMove(Player player) throws InterruptedException {
        displayString(player + " has a forced move.");
        TimeUnit.SECONDS.sleep(1);
    }
    
    // ----- END OF MOVE RESPOND MESSAGES -----
    
    // ----- ERROR RESPOND MESSAGES -----
    
    public void displayError_WrongScoreToWinEntered() {
    	displayString("Sorry, that number of points is not possible. Please enter a number greater 0, but less or equal than the number of players pips");
    }
    
    public void displayError_WrongInputForNumberOfPointsPlayingTo() throws InterruptedException {
    	displayString("Wrong input for number of moves, please try again");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    public void displayError_incorrectEntry() throws InterruptedException {
    	displayString("Invalid Command : Please try again");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    public void displayError_WrongInputGivenForRepeatingOrEndingGame() throws InterruptedException {
    	displayString("Wrong input, please enter 'yes' or 'no', please try again");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    // ----- END OF ERROR RESPOND MESSAGES -----
    
    // ----- END GAME MESSAGES -----
    public void display_roundWinner(Player winner) {
    	displayString(winner.toString() + " win this match! Keep it up!");
    }
    
    public void display_PlayersWantNextMatch() throws InterruptedException {
    	displayString("Next match will start in 1 second\n");
    	displayString("=============== NEW MATCH ===============");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    public void display_PlayersWantEndGame() {
    	displayString("Players want to end game");
    }
    
    public void display_endGame() throws InterruptedException {
    	displayString("=============== END GAME ===============");
    	TimeUnit.SECONDS.sleep(3);
    }
    
    public void display_endMatch() throws InterruptedException {
    	displayString("=============== END MATCH ===============");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    /**
     * Method to display the winner of the whole game
     * @param player_id		Winner's player ID
     * @param playerName	Winner's name
     */
    public void display_gameWinner(String playerName) {
    	displayString("Player " +  playerName + " HAS WON! CONGRAT !!");
    }
    
    /**
     * Method to display the message when the game end by rejecting the double play
     * @param winnerName	
     * @param loserName
     * @param earnScore The match score the winner earns in the recent match	
     */
    public void print_rejectedDoubleTheScore(String winnerName , String loserName , int earnScore) {
    	displayString(winnerName + " has won, and " + loserName + " has lost the match. " + winnerName + 
    			" has gained " + earnScore + " points in current game turn.");
    }
    // ----- END OF END GAME MESSAGES -----
    
    // Information panel (left panel)
    public void updatePointsArePlayingTo(int pointsArePlayingTo) {
    	gameInfoPanel.setScoreArePlayingUpTo(pointsArePlayingTo);
    }
    
    public void updatePointsOfPlayer1(int points) {
    	gameInfoPanel.setScoreOfPlayer1(points);
    }
    
    public void updatePointsOfPlayer2(int points) {
    	gameInfoPanel.setScoreOfPlayer2(points);
    }
    
    public void updatePointsOfPlayer(int playerNumber, int points) {
    	if(playerNumber == 0) {
    		// is player 1
    		updatePointsOfPlayer1(points);
    	}else {
    		// is player 2
    		updatePointsOfPlayer2(points);
    	}
    }
    
    public void doubleTheDoubleCube() {
    	gameInfoPanel.doubleTheGameScore();
    }
    
    public void giveDoubleCubeTo(int playerNumber) {
    	gameInfoPanel.setThelayerWhoOwnsDoubleDice(playerNumber);
    }
    
    public void updateInfoPanel() {
    	gameInfoPanel.updateInfoPanel();
    }
}
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class UI {
    // UI is the top level interface to the user interface

    private static final int FRAME_WIDTH = 1100;
    private static final int FRAME_HEIGHT = 600;

    private final BoardPanel boardPanel;
    private final InfoPanel infoPanel;
    private final CommandPanel commandPanel;

    UI (Board board, Players players) {
        infoPanel = new InfoPanel();
        commandPanel = new CommandPanel();
        JFrame frame = new JFrame();
        boardPanel = new BoardPanel(board,players);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setTitle("Backgammon");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(boardPanel, BorderLayout.LINE_START);
        frame.add(infoPanel, BorderLayout.LINE_END);
        frame.add(commandPanel, BorderLayout.PAGE_END);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public String getString() {
        return commandPanel.getString();
    }

    public void display() {
        boardPanel.refresh();
    }

    public void displayString(String string) {
        infoPanel.addText(string);
    }

    public void displayStartOfGame() {
        displayString("Welcome to Backgammon");
    }

    public void promptPlayerName() {
        displayString("Enter a player name:");
    }
    
    public void promptPlayersForNumberOfPointsWantToPlayTo() {
    	displayString("Enter the number of points are playing up to:");
    }
    
    public void promptPlayersIfWantToPlayAgain() {
    	displayString("Do you wish to play again? (enter yes/no)");
    }
    
    public void promptPlayersIfWantToPlayNextGame() {
    	displayString("Do you wish to play next game? (enter yes/no)");
    }
    
    public void promptPlayerIfWantToDouble() {
    	displayString("Do you want to double?");
    }
    
    public void promptPlayerifWantToAcceptDouble() {
    	displayString("Do you want to accept the double? (enter yes/no)");
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
        displayString(player + " wins the roll and goes first.");
    }

    public void displayGameWinner(Player player) {
        displayString(player + " WINS THE GAME!!!");
    }

    public void promptCommand(Player player) {
        displayString(player + " (" + player.getColorName() + ") enter your move or quit:");
    }
    
    public Command getCommand(Plays possiblePlays) {
        Command command;
        do {
            String commandString = commandPanel.getString();
            command = new Command(commandString,possiblePlays);
            if (!command.isValid()) {
                displayString("Error: Command not valid.");
            }
        } while (!command.isValid());
        return command;
    }

    public void displayPlays(Player player, Plays plays) {
        displayString(player + " (" + player.getColorName() + ") available moves...");
        int index = 0;
        for (Play play : plays) {
            String code;
            if (index<26) {
                code = "" + (char) (index%26 + (int) 'A');
            } else {
                code = "" + (char) (index/26 - 1 + (int) 'A') + (char) (index % 26 + (int) 'A');
            }
            displayString(code + ". " + play);
            index++;
        }
    }

    public void displayNoMove(Player player) throws InterruptedException {
        displayString(player + " has no valid moves.");
        TimeUnit.SECONDS.sleep(1);
    }

    public void displayForcedMove(Player player) throws InterruptedException {
        displayString(player + " has a forced move.");
        TimeUnit.SECONDS.sleep(1);
    }
    
    // Errors
    
    public void displayError_WrongInputForNumberOfPointsPlayingTo() throws InterruptedException {
    	displayString("Wrong input for number of moves, please try again");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    public void displayError_incorrectEntry() throws InterruptedException {
    	displayString("woops, cans you please enter that again");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    public void displayError_WrongInputGivenForRepeatingOrEndingGame() throws InterruptedException {
    	displayString("Wrong input, please enter 'yes' or 'no', please try again");
    	TimeUnit.SECONDS.sleep(1);
    }
    
    public void display_PlayersWantNextGame() {
    	displayString("Next match will start");
    }
    
    public void display_PlayersWantToEndGame() {
    	displayString("Players want to end game");
    }
    
    public void display_CurrentPlayersScores(int player1_winCount, int player2_winCount) {
    	displayString("Player 1 has a score of: " + player1_winCount + ", and player 2 has a score of: " + player2_winCount);
    }
    
    public void displayError_WrongScoreToWinEntered() {
    	displayString("Sorry, that number of points is not possible. Please enter a number greater 0, but less or equal than the number of players pips");
    }
    
    public void print_doubleTheScore() {
    	displayString("the score has been doubled");

    }
    
    public void print_rejectedDoubleTheScore(String nameOfPlayer, int newScore, int pointsLost) {
    	displayString(nameOfPlayer + " you have rejected the double and lost " + pointsLost + 
    			". You now have a score of " + newScore);

    }
    
    public void print_endFullGameMessage(int player_id, String playerName) {
    	displayString("player " +  player_id + " has won. Well done " + playerName);

    }
    
    public void print_CurrentPlayer(String playerName) {
    	displayString("player " +  playerName + ", it is now your turn.");
    }
}
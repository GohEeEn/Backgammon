import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GameInformationPanel extends JPanel {
	
	// constants
	
	// variables
	// Display
	private JTextArea playerWhoOwnsDoublingDice;
	
	private DoubleingDice doubleingCube;
	
	private JTextArea scorePlayingUpTo;
	
	private JTextArea scoreOfGame; 
	
	private int scoreArePlayingUpTo;
	private int player1Score;
	private int player2Score;
	
	private int currentPlayerWhoHasDoublingDice;
	
	
	
	
	// Constructurs
	public GameInformationPanel() {
		// Set up doubling cube in center (vert)
		// set scorePlayingUpTo above the doubling cube
		// set the scoreOfGame below the doubling cube
		
		// set up base JPanel (this)
        this.setPreferredSize(new Dimension(150, 600));
        setBackground(Color.YELLOW);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        resetInformation();	// Initialise
        
        // set up display of score playing up to
        scorePlayingUpTo = new JTextArea();
        scorePlayingUpTo.setEnabled(false);
        scorePlayingUpTo.setSize(100, 100);
        scorePlayingUpTo.setBackground(Color.blue);
        String display_scoreArePlayingTO = "Score playing up to: \n " + scoreArePlayingUpTo;
        scorePlayingUpTo.setText(display_scoreArePlayingTO);
        this.add(scorePlayingUpTo);
        
        // set up display of who owns the double dice
        playerWhoOwnsDoublingDice = new JTextArea();
        playerWhoOwnsDoublingDice.setEnabled(false);
        playerWhoOwnsDoublingDice.setSize(100, 100);
        playerWhoOwnsDoublingDice.setBackground(Color.blue);
        String display_whoOwnsDoubleDice = "player who has double dice: \n" + currentPlayerWhoHasDoublingDice;
        playerWhoOwnsDoublingDice.setText(display_whoOwnsDoubleDice);
        this.add(playerWhoOwnsDoublingDice);
        
        
        
        // set up display of doubling cube
        doubleingCube = new DoubleingDice();
        doubleingCube.setSize(100, 100);
        this.add(doubleingCube);
        
        // set up display of score of each player
        scoreOfGame = new JTextArea(); 
        scoreOfGame.setEnabled(false);
        scoreOfGame.setSize(100, 10);
        scoreOfGame.setBounds(0, 0, 10, 10);
        scoreOfGame.setBackground(Color.yellow);
        String display_scoreOfEachPlayer = "Score is: \n player 1: " + player1Score + "\n player 2: " + player2Score;
        scoreOfGame.setText(display_scoreOfEachPlayer);		
        this.add(scoreOfGame);

	}
	
	public void resetInformation() {
		scoreArePlayingUpTo = 0;
		player1Score = 0;
		player2Score = 0;
	}
	
	//methods
	
	// Getters and setters
	public void setScoreArePlayingUpTo(int theScore) {
		this.scoreArePlayingUpTo = theScore;
	}
	
	public void setScoreOfPlayer1(int scoreOfPlayer1) {
		this.player1Score = scoreOfPlayer1;
	}
	
	public void setScoreOfPlayer2(int scoreOfPlayer2) {
		this.player2Score = scoreOfPlayer2;
	}
	
	public void setScoreOfPlayer1And2(int scoreOfPlayer1, int scoreOfPlayer2) {
		this.player1Score = scoreOfPlayer1;
		this.player2Score = scoreOfPlayer2;
	}
	
	public void setThelayerWhoOwnsDoubleDice(int playerNumber) {
		currentPlayerWhoHasDoublingDice = playerNumber;
	}
	// end of getters and setters
	
	public void doubleTheGameScore() {
		doubleingCube.doubleTheCube();
	}
	
	public void updateInfoPanel() {
		// Update all information componenets
		// Update score are playing up to
		scorePlayingUpTo.setText("Score playing up to: \n " + scoreArePlayingUpTo);
		
		// update who owns the doubleing dice
		playerWhoOwnsDoublingDice.setText("player who has double dice: \n" + currentPlayerWhoHasDoublingDice);
		
		// update the current score
        String display_scoreOfEachPlayer = "Score is: \n player 1: " + player1Score + "\n player 2: " + player2Score;
		scoreOfGame.setText(display_scoreOfEachPlayer);
		
		// update the doubling cube
		doubleingCube.updateDiceDisplay();
	}

}

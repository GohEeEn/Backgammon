package gui;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class EndGame {
	
	// Variables

	private BorderPane popUpWindow;	
	private TextArea announcement;
	
	// ----- Buttons Available ----
	private Button rematch_Btn;
	private Button newGame_Btn;
	private Button endGame_Btn;
	
	// End of display ^^^
	
	// End of variables
	
	/**
	 * Construction
	 * @param winner 
	 */
	public EndGame(String winner) {
		setUp(winner);	
	}
	
	private void setUp(String winner) {
		
		// Set up the pop up window displaying winner
		final double popUpWindow_Width = 500;
		final double popUpWindow_Height = 500;
		
		popUpWindow = new BorderPane();
		
		popUpWindow.setPrefSize(popUpWindow_Width, popUpWindow_Height);
		
		
		String endGameMessage = "The winner is: " + winner;
		
		announcement = new TextArea();
		announcement.setText(endGameMessage);
		announcement.setPrefSize(500, 50);
		
		// ----- Buttons Setup -----
		
		/*
		 * Rematch Button and Functionality
		 * - Same players profile
		 */ 
		rematch_Btn = new Button("REMATCH");
		rematch_Btn.setPrefSize(500, 100);
		rematch_Btn.setOnAction(value ->  {

         });
		
		/*
		 * Start a new game Button and Functionality
		 */ 
		newGame_Btn = new Button("NEW GAME");
		newGame_Btn.setPrefSize(500, 100);
		newGame_Btn.setOnAction(value ->  {
            // New game 
         });
		
		/*
		 * End Game Button and Functionality
		 */ 
		endGame_Btn = new Button("END GAME");
		endGame_Btn.setPrefSize(500, 100);
		endGame_Btn.setOnAction(value ->  {
			System.exit(0);
         });
		
		// Attach components to pop up window
		popUpWindow.setTop(announcement);
		VBox buttonContainer = new VBox();
		buttonContainer.getChildren().addAll(rematch_Btn, newGame_Btn, endGame_Btn);
		popUpWindow.setCenter(buttonContainer);
		
	}
	
	/**
	 * Method to create a pop-up window to indicate the end of game and the announce of winner
	 * @return	The pop-up window
	 */
	public BorderPane getEndGame_PopUp() {
		return popUpWindow;
	}
	
	// END OF CONSTRUCTION
	
	// Methods
	
	
	
	
	
	
	
	
	// End methods
	
	
	
	

}

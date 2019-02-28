
package game;

import gui.Board;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 * @author YeohB - 17357376
 *
 */

public class Main extends Application {

	/** Instance variables */
	Board board;

	/** Set stage upon running the program */
	@Override
	public void start(Stage primaryStage) throws Exception {

		BorderPane root = new BorderPane();
		
		GameController gameController = new GameController(); 
		
		/* Place the game board in the center of the root */
		root.setCenter(gameController.getGameContainer());
		
		/* Place the text board in the bottom of the root */
		root.setBottom(gameController.getTextBox());

		root.getStyleClass().add("root");
	
		Scene scene = new Scene(root, 1100, 1000);

		primaryStage.setTitle("Backgammon");	// Set the application name 
		primaryStage.setScene(scene);	
		primaryStage.setResizable(false);		// The application interface can't be resized
		primaryStage.show();					
	}

	/**
	 * Launch the program (main method)
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}

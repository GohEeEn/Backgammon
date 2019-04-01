package gui;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A class that in charge of the functionality and GUI definition of command input and output text box
 * 
 * @author Ee En Goh - 17202691
 * @author Ferdia Fagan - 16372803
 */
public class TextBox {

	// ----- CONSTANTS (Size of the text box) -----
	private final double WIDTH_OFWHOLECONTAINER = 200;
	private final double WIDTH_OFROLLBUTTON = 150;
	private final double HEIGHT_OFWHOLECONTAINER;
	private final double HEIGHT_OFINPUTCONTAINER = 100;
	// ----- END OF CONSTANTS -----
	
	// ----- VARIABLES -----
	BorderPane textContainer;

	TextField inputField;
	TextArea outputField;

	Button rollDiceBtn;

	HBox inputBox;
	HBox outputBox;
	// ----- END OF VARIABLES -----
	
	/** A boolean value that used to check if the dice roll red button is currently disabled */
	private boolean diceRollDisabled;

	public TextBox(double heightOfScreen) {
		
		HEIGHT_OFWHOLECONTAINER = heightOfScreen;
		
		textContainer = new BorderPane();
		
		inputField = new TextField();
		
		outputField = new TextArea();
		rollDiceBtn = new Button();
		
		inputBox = new HBox(inputField, rollDiceBtn);
		outputBox = new HBox(outputField);

		diceRollDisabled = false; // TODO
		
		initTextBox();
	}

	/**
	 * Set up text box
	 */
	private void initTextBox() {

		HBox.setHgrow(inputField, Priority.ALWAYS);
		HBox.setHgrow(outputField, Priority.ALWAYS);

		inputField.setPrefSize(WIDTH_OFWHOLECONTAINER, HEIGHT_OFINPUTCONTAINER);
		outputField.setPrefSize(WIDTH_OFWHOLECONTAINER,HEIGHT_OFWHOLECONTAINER - HEIGHT_OFINPUTCONTAINER);

		/* Initialize the outputField  */
		
		outputField.setText(">> Welcome to Backgammon <<");
		outputField.setEditable(false);
		outputField.setWrapText(true);
		outputField.setScrollTop(Double.MAX_VALUE); // TODO

		rollDiceBtn.setText("Roll Dice");
		//rollDiceBtn.setPrefWidth(200.0);
		//rollDiceBtn.setPrefHeight(50.0);
		//textContainer.setPrefHeight(200);
		
		rollDiceBtn.setPrefSize(WIDTH_OFROLLBUTTON, HEIGHT_OFINPUTCONTAINER);
		textContainer.setPrefSize(WIDTH_OFWHOLECONTAINER,HEIGHT_OFWHOLECONTAINER);
		
		textContainer.setCenter(outputBox);
		textContainer.setBottom(inputBox);

		rollDiceBtn.setStyle("-fx-font-size: 30px;");
		outputField.setStyle("-fx-text-fill: black;");
		rollDiceBtn.setStyle("-fx-background-color: #d11919;");
	}

	/**
	 * Method to output general message on output field
	 * @param s	General message given
	 */
	public void generalOutput(String s) {
		outputField.appendText("\n" + s);
	}
	
	/**
	 * Method to output customized feedback message on output field
	 * @param s	Customized feedback given
	 */
	public void output(String s) {
		outputField.appendText("\n> " + s);
	}
	
	/**
	 * Method to display standard error message<br>
	 * "move"    - Invalid move<br>
	 * "input"   - Invalid input<br>
	 * "command" - Invalid command<br>
	 * @param error	The keyword that specify the type of error
	 */
	public void outputError(String error) {
		String s = "";
		
		switch (error.toLowerCase()) {
			case "move":
				s = "Invalid move, please try again\n";
				break;
			case "input":
				s = "Invalid input, please try again\n";
				break;
			case "command":
				s = "Invalid command, please try again\n";
				break;
		}
		output(s);
	}
	
	/**
	 * Method to display defined warning messages, based on the warning statement given (parameter)<br>
	 * "dice"	- Using all the dice roll before end the game turn<br>
	 * "name"	- All players must provide a name first<br>
	 * "roll"	- Roll dice (red button) before this command<br>
	 * @param warning	The warning statement given, that specify the the purpose of warning message
	 */
	public void warningMessage(String warning) {
		String output = "WARNING: ";

		switch (warning.toLowerCase()) {
			case "dice":
				output += "You must use ALL dice rolls before continuing\n";
				break;
			case "name": 
				output += "All players must provide a name first\n";
			case "roll":
				output += "You must ROLL DICE (red button) before this command \n";
				break;
		}
		
		System.out.println("\tWarning message created\t\t: SUCCESS"); // Testing
		output(output);
	}
	
	/**
	 * Method to display all the commands available in this program to use their functionalities
	 * @param command	The related keyword to get the command required 
	 */
	public void displayHelp(String command) {

		String output = "\t< Commands >\n\tUSAGE : ";

		switch (command.toLowerCase()) {
			case ".name":
				output += "To change names\t- .name [oldName] [newName]";
				break;
			case ".move":
				output += "To move\t- .move [moveFrom] [moveTo]";
				break;
			case "quit":
				output += "To quit\t- quit";
				break;
			case "next":
				output += "To end turn\t- next";
				break;
			case "all":
				output += "To change names\t\t- .name [oldName] [newName]\n" + "\tUSAGE : To move\t\t\t\t- .move [moveFrom] [moveTo]\n" + "\tUSAGE : To move without rules\t- cheat [moveFrom] [moveTo]\n" + "\tUSAGE : To quit\t\t\t\t- quit\n" + "\tUSAGE : To end turn\t\t\t- next\n";
				break;
		}

		output(output);
	}
	
	// ----- Input Field Methods -----

	/**
	 * Method to disable the clicking of dice-roll red button 
	 * @param disable	Boolean value to indicate the disable of the dice-roll button
	 */
	public void disableDiceRollBtn(boolean disable) {
		this.diceRollDisabled = disable;
		this.rollDiceBtn.setDisable(disable);
	}

	/**
	 * Method to clear the input box, for the convenience of next command input
	 */
	public void clearInputField() {
		this.inputField.setText("");
	}
	
	/**
	 * Method to display the player's command input with the current player's name
	 * @param player	The name of the current player
	 */
	public void printUserInput(String player) {
		outputField.appendText("\n\n[@" + player + "] " + getUserInput());
		clearInputField();
	}
	
	// ----- End of Input Field Methods -----
	
	// ----- Getter Methods ------
	
	/**
	 * Method to check if the dice roll button is disabled
	 * @return	True if the dice roll button is disabled, else false
	 */
	public boolean getDiceRollBtnDisabled() {
		return this.diceRollDisabled;
	}
	
	/**
	 * Method to get input from user, command or normal message
	 */
	public String getUserInput() {
		return inputField.getText();
	}
	/**
	 * @return the BorderPane that contains all the I/O graphs
	 */
	public BorderPane getTextBox() {
		return this.textContainer;
	}

	/**
	 * @return the area where the user inputs text
	 */
	public TextField getInputField() {
		return this.inputField;
	}

	/**
	 * @return the area where output text goes
	 */
	public TextArea getOutputField() {
		return this.outputField;
	}

	/**
	 * @return the dice button
	 */
	public Button getDiceButton() {
		return this.rollDiceBtn;
	}
	
	// ----- End of Getter Methods ------
	
	// ----- FUNCTIONALITY -----
	public void printPossibleMoves(String[] possibleMoves) {
		// Print eg number + move
		for (int i = 0; i < possibleMoves.length;i++) {
			this.output(possibleMoves[i]);
		}
	}
	// ----- END OF FUNCTIONALITY -----
}

package gui;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A class that in charge of the functionality and GUI definition of command input and output text box
 * @author YeohB - 17357376
 * @author Ee En Goh - 17202691
 */
public class TextBox {

	BorderPane textContainer;

	TextField inputField;
	TextArea outputField;

	Button rollDiceBtn;

	HBox inputBox;
	HBox outputBox;

	public TextBox() {
		textContainer = new BorderPane();
		
		inputField = new TextField();
		
		outputField = new TextArea();
		rollDiceBtn = new Button();
		
		inputBox = new HBox(inputField, rollDiceBtn);
		outputBox = new HBox(outputField);

		initTextBox();
	}

	/**
	 * Set up text box
	 */
	private void initTextBox() {

		HBox.setHgrow(inputField, Priority.ALWAYS);
		HBox.setHgrow(outputField, Priority.ALWAYS);

		inputField.setPrefHeight(50);
		outputField.setPrefHeight(100);

		outputField.setText("> Welcome to Backgammon");
		outputField.setEditable(false);
		outputField.setWrapText(true);
		outputField.setScrollTop(Double.MIN_VALUE);

		rollDiceBtn.setText("Roll Dice");
		rollDiceBtn.setPrefWidth(200.0);
		rollDiceBtn.setPrefHeight(50.0);

		textContainer.setPrefHeight(100);
		textContainer.setCenter(outputBox);
		textContainer.setBottom(inputBox);

		rollDiceBtn.setStyle("-fx-font-size: 30px;");
		outputField.setStyle("-fx-text-fill: black;");
		rollDiceBtn.setStyle("-fx-background-color: #d11919;");
	}
	/**
	 * Method to output customized message on output field
	 * @param s	The given message to displayed on the output field
	 */
	public void output(String s) {
		outputField.appendText("\n> " + s);
	}
	
	/**
	 * Method to display standard error message
	 * @param error	The keyword that specify the type of error
	 */
	public void outputError(String error) {
		String s = "";
		
		switch (error) {
			case "move":
				s = "Invalid move, please try again";
				break;
			case "input":
				s = "Invalid input, please try again";
				break;
			case "command":
				s = "Invalid command, please try again";
				break;
		}
		
		output(s);
	}
	
	/**
	 * Method to display defined warning messages, based on the warning statement given (parameter)
	 * @param warning	The warning statement given, that specify the the purpose of warning message
	 */
	public void warningMessage(String warning) {
		String output = "WARNING: ";

		switch (warning) {
			case "dice":
				output += "You must use all dice rolls before continuing";
				break;
			case "name": 
				output += "All players must provide a name first";
		}
		output(output);
	}
	
	/**
	 * Method to display all the commands available in this program to use their functionalities
	 * @param command	The related keyword to get the command required 
	 */
	public void displayHelp(String command) {

		String output = "\t\t< Commands >\n\tUSAGE : ";

		switch (command) {
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
				output += "To change names\t- .name [oldName] [newName]\n" + "\tUSAGE : To move\t- .move [moveFrom] [moveTo]\n" + "\tUSAGE : To quit\t- quit\n" + "\tUSAGE : To end turn\t- next\n";
				break;
		}

		output(output);
	}
	
	/**
	 * Method to disable the clicking of dice-roll red button 
	 * @param disable	Boolean value to indicate the disable of the dice-roll button
	 */
	public void disableDiceRollBtn(boolean disable) {
		this.rollDiceBtn.setDisable(disable);
	}

	/**
	 * Method to get input from user, command or normal message
	 */
	public String getUserInput() {
		return inputField.getText();
	}
	
	/**
	 * Method to display the player's command input with the current player's name
	 * @param player	The name of the current player
	 */
	public void printUserInput(String player) {
		outputField.appendText("\n[@" + player + "] " + getUserInput());
		clearInputField();
	}
	
	/**
	 * Method to clear the input box, for the convenience of next command input
	 */
	public void clearInputField() {
		this.inputField.setText("");
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
}

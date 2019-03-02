package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Class that define the GUI of the game board
 * @author YeohB - 17357376
 * @author Ee En Goh - 17202691
 */

public class Board {
	
	/* The whole container of UI of this game */
	private HBox gameContainer;
	
	private VBox board;

	/* Upper Part : Game Board */
	
	private BorderPane leftBoard ;
	private BorderPane rightBoard ;

	private BorderPane topBorder ;
	private BorderPane bottomBorder;
	
	/** Vertical Box that the home checkers removed to when bear off happens */
	private VBox sideBox;
	
	private HBox labelContainer;
	
	private Jail jail ;
	private Pip pip ;
	private Pip[] pipArray ;
	
	private HBox topRightQuad;
	private HBox topLeftQuad;
	private HBox bottomLeftQuad;
	private HBox bottomRightQuad;

	private HBox topLeftLabelContainer;
	private HBox topRightLabelContainer;
	private HBox bottomLeftLabelContainer;
	private HBox bottomRightLabelContainer;
	
	
	
	/**
	 * Constructor to initialise the whole game
	 */
	public Board() {
		
		gameContainer = new HBox();
		board = new VBox();

		topBorder = new BorderPane();
		bottomBorder = new BorderPane();

		leftBoard = new BorderPane();
		rightBoard = new BorderPane();

		jail = new Jail();
		sideBox = new VBox();
		
		topLeftLabelContainer = new HBox();
		topRightLabelContainer = new HBox();
		bottomLeftLabelContainer = new HBox();
		bottomRightLabelContainer = new HBox();
		
		pipArray = new Pip[24];
		
		initBoard();
		initPipNumber();
	}

	/**
	 * Initialize and set up the game board
	 */
	private void initBoard() {

		HBox middle = new HBox();

		HBox topLeftLabel = new HBox();
		topLeftLabel.setPrefWidth(475);

		gameContainer.setPrefWidth(1100);
		board.setPrefWidth(1000);
		topBorder.setPrefSize(900, 100);
		bottomBorder.setPrefSize(900, 100);

		leftBoard.setPrefSize(475, 600);
		rightBoard.setPrefSize(475, 600);
		//rightBoard.setRight(sideBox);
		leftBoard.setRight(jail);

		initPips();
		initSideBox();

		middle.getChildren().addAll(leftBoard, jail, rightBoard);
		board.getChildren().addAll(topBorder, middle, bottomBorder);
		gameContainer.getChildren().addAll(board, sideBox);

		topBorder.setStyle("-fx-background-color: #42210B;");
		bottomBorder.setStyle("-fx-background-color: #42210B;");

		leftBoard.setStyle("-fx-background-color: #A67C52;");
		rightBoard.setStyle("-fx-background-color: #A67C52;");
	}
	
	/**
	 * Set up the container to store the doubling cube and disks for future sprints
	 */
	private void initSideBox() {

		BorderPane diskArea = new BorderPane();
		diskArea.setPrefHeight(600);

		sideBox.setPrefWidth(100);
		sideBox.setAlignment(Pos.CENTER);
		sideBox.getChildren().add(diskArea);

		VBox doublingCube = new VBox();
		StackPane blackHome = new StackPane();
		StackPane whiteHome = new StackPane();

		doublingCube.setPrefSize(50, 50);
		blackHome.setPrefSize(50, 250);
		whiteHome.setPrefSize(50, 250);

		diskArea.setTop(blackHome);
		diskArea.setCenter(doublingCube);
		diskArea.setBottom(whiteHome);

		doublingCube.setStyle("-fx-border-color: white; -fx-border-width: 5px;");
		blackHome.setStyle("-fx-border-color: white; -fx-border-width: 5px;");
		whiteHome.setStyle("-fx-border-color: white; -fx-border-width: 5px;");

		sideBox.setStyle("-fx-background-color: #42210B;");
		diskArea.setStyle("-fx-border-color: black; -fx-border-width: 8px; -fx-background-color: #5b3208;");
	}

	/**
	 * Initialize pips on their starting position
	 */
	private void initPips() {

		double rotatePip = 180.0;		
		Color color = Color.BLACK;

		for (int i = 0; i < 24; i++) {
			if (i >= 12) {
				rotatePip = 0;	
			}

			if (i % 2 == 0) {
				color = Color.WHITE;
			}

			else if (i % 2 == 1) {
				color = Color.BLACK;
			}

			pipArray[i] = new Pip(i);
			pipArray[i].initPip(color, rotatePip);

			switch (i) {
			
				case 0:
					pipArray[i].initDisks(2, Color.WHITE, Color.BLACK);
					break;
				case 5:
					pipArray[i].initDisks(5, Color.BLACK, Color.WHITE);
					break;
				case 7:
					pipArray[i].initDisks(3, Color.BLACK, Color.WHITE);
					break;
				case 11:
					pipArray[i].initDisks(5, Color.WHITE, Color.BLACK);
					break;
				case 12:
					pipArray[i].initDisks(5, Color.BLACK, Color.WHITE);
					break;
				case 16:
					pipArray[i].initDisks(3, Color.WHITE, Color.BLACK);
					break;
				case 18:
					pipArray[i].initDisks(5, Color.WHITE, Color.BLACK);
					break;
				case 23:
					pipArray[i].initDisks(2, Color.BLACK, Color.WHITE);
					break;
			}
		}
		updatePips();
	}

	/**
	 * Set and update each HBox of pips to their respective quadrants
	 */
	private void updatePips() {
		
		HBox topRightQuad = createSetOfPoints(19, 24);
		HBox topLeftQuad = createSetOfPoints(13, 18);
		HBox bottomLeftQuad = createSetOfPoints(12, 7);
		HBox bottomRightQuad = createSetOfPoints(6, 1);
		
		rightBoard.setBottom(bottomRightQuad);
		leftBoard.setBottom(bottomLeftQuad);
		leftBoard.setTop(topLeftQuad);
		rightBoard.setTop(topRightQuad);
	}

	/**
	 * Position each pip within a HBox so it can be accurately positioned without coding individual pips
	 * @param leftNumber		The left-most pip index in the given label container
	 * @param rightNumber		The right-most pip index in the given label container
	 * @return	The HBox with the pips inserted
	 */
	private HBox createSetOfPoints(int leftValue, int rightValue) {

		HBox pipContainer = new HBox();

		pipContainer.setAlignment(Pos.CENTER);
		pipContainer.setPrefSize(leftBoard.getPrefWidth(), 250.0);
		pipContainer.setSpacing((leftBoard.getPrefWidth() - 6 * (40.0)) / 6);

		if (leftValue > rightValue) {
			for (int i = leftValue - 1; i >= rightValue - 1; i--) {
				pipContainer.getChildren().add(pipArray[i]);
			}
		}
		else {
			for (int i = leftValue - 1; i < rightValue; i++) {
				pipContainer.getChildren().add(pipArray[i]);
			}
		}

		return pipContainer;
	}
	
	/**
	 * Initialize the pip index to each container for display later 
	 */
	private void initPipNumber() {
		
		topBorder.setLeft(labelPip(13, 18, topLeftLabelContainer));
		topBorder.setRight(labelPip(19, 24, topRightLabelContainer));
		bottomBorder.setLeft(labelPip(12, 7, bottomLeftLabelContainer));
		bottomBorder.setRight(labelPip(6, 1, bottomRightLabelContainer));
	}

	/**
	 * Label the pips correctly and accurately by using HBox enables to have nice graphics in the future sprints and accurate spacing
	 * @param leftNumber		The left-most pip index in the given label container
	 * @param rightNumber		The right-most pip index in the given label container
	 * @param labelContainer	The given label container
	 * @return	HBox with the pip index inserted and ready for display
	 */
	private HBox labelPip(int leftNumber, int rightNumber, HBox labelContainer) {
		
		if (leftNumber > rightNumber) {
			for (int i = leftNumber; i >= rightNumber; i--) {
				Label label = new Label(Integer.toString(i));
				HBox box = new HBox(label);

				box.setAlignment(Pos.CENTER);
				box.setPrefWidth(78.5);

				labelContainer.getChildren().add(box);

				label.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");
			}
		}

		else {
			for (int i = leftNumber; i <= rightNumber; i++) {
				Label label = new Label(Integer.toString(i));
				HBox box = new HBox(label);

				box.setAlignment(Pos.CENTER);
				box.setPrefWidth(78.5);

				labelContainer.getChildren().add(box);

				label.setStyle("-fx-font-size: 30px; -fx-text-fill: white;");
			}
		}

		return labelContainer;
	}
	
	public void setPipLabelRegion(double rotatation) {
		if (rotatation == 0) {	
			topBorder.setLeft(topLeftLabelContainer);
			topBorder.setRight(topRightLabelContainer);
			bottomBorder.setLeft(bottomLeftLabelContainer);
			bottomBorder.setRight(bottomRightLabelContainer);
		}

		else if (rotatation == 180) {
			topBorder.setLeft(bottomLeftLabelContainer);
			topBorder.setRight(bottomRightLabelContainer);
			bottomBorder.setLeft(topLeftLabelContainer);
			bottomBorder.setRight(topRightLabelContainer);
		}
	}
	
	/**
	 * Add the disk that got hit to Jail
	 * @param moveFrom	The index of pip where the disk hit happen
	 */
	public void addToJail(int moveFrom) {
		jail.push(getPipArray(moveFrom).updatePoppedDisks());
		jail.updateJail();
	}
	

	/**
	 * Move disk from user selection to a valid entry
	 * @param moveFromIndex
	 * @param moveToIndex
	 */
	public void moveDisks(int moveFromIndex, int moveToIndex) {
		
		if (!pipArray[moveFromIndex].isEmpty()) {
			pipArray[moveToIndex].updatePushedDisks(pipArray[moveFromIndex].updatePoppedDisks());
		}
	}
	
	/**
	 * @return VBox that contains the game GUI
	 */
	public HBox getGameContainer() {
		return this.gameContainer;
	}

	/**
	 * @param index The pip index of the pip to be returned
	 * @return The pip specified at index
	 */
	public Pip getPipArray(int index) {
		return this.pipArray[index];
	}

	/**
	 * @return The vbox that will hold the suspended disks
	 */
	public Jail getJail() {
		return this.jail;
	}
	
	public VBox getBoard() {
		return this.board;
	}
	
	public BorderPane getLeftBoard() {
		return this.leftBoard;
	}
	
	public BorderPane getRightBoard() {
		return this.rightBoard;
	}
	
	/**
	 * Method to get the colour of disks on any pip on the game board with the given pip index
	 * @param pipIndex	The given pip index ( 0 - 23 )
	 * @return	The colour of disks on any pip
	 */
	public String getDiskColorOnPip(int pipIndex) {
		return pipArray[pipIndex].returnDiskColor();
	}
}

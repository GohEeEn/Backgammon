package gui;

import data_structures.LinkedStack;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PipHome extends LinkedStack<Disks> {

	/** Border of jail in GUI */
	private Rectangle rectangle;

	PipHome(double width, double height) {
		setPrefSize(width, height);
		setAlignment(Pos.CENTER);
		setStyle("-fx-border-color: white; -fx-border-width: 5px;");
	}

	/**
	 * @return the shape of the jail
	 */
	public Rectangle getRectangle() {
		return this.rectangle;
	}

	// Function methods
	
	public void updateHome() {
		int i = 1;
		for (Disks disk : this) {
			disk.getCircle().setTranslateY(0 + (i++ * 40));

			getChildren().add(disk.getCircle());
		}
	}
	
	
	
	
	// end of function methods
}

package gui;

import data_structures.LinkedStack;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The instance of Jail class is the gap area between the left and right boards, which is also the position to place the hit checker
 * 
 * @author Ee En Goh - 17202691
 */
public class Jail extends LinkedStack<Disks> {

	/** Border of jail in GUI */
	private Rectangle rectangle;
	
	Jail() {
		setPrefSize(50, 200);
		setAlignment(Pos.CENTER);
		setStyle("-fx-background-color: #C19A6B;");
	}
	
	/**
	 * @return the shape of the jail
	 */
	public Rectangle getRectangle() {
		return this.rectangle;
	}

	/**
	 * Update UI to separate the black disks to white disks in the jail
	 */
	public void updateJail() {
		getChildren().clear();
		
		int i = 1;
		int j = 1;
		
		for (Disks disk : this) {
			if (disk.getColor() == Color.BLACK) {
				disk.getCircle().setTranslateY(0 + (i++ * 40));
				System.out.println("i: " + i);
			}

			else if (disk.getColor() == Color.WHITE) {
				disk.getCircle().setTranslateY(0 - (j++ * 40));
				System.out.println("j: " + j);
				
			}

			getChildren().add(disk.getCircle());
		}
	}

	
	public Color diskColorInJail() {
		return (Color)top().getColor();
	}
}

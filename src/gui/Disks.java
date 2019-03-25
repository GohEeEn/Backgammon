package gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 * Disk class that include the UI definition of a disk and its basic operations
 * @author Ferdia Fagan - 16372803
 * @author Ee En Goh - 17202691
 */

public class Disks {

	/* Instance fields */
	
	/** Each disk has the shape in circle */
	private Circle circle;
	private static double CircleSize;
	
	
	
	// setup
	
	public static void SetDiskSize(double radius) {
		CircleSize = radius;
	}
	
	
	/**
	 * Constructor that creates a new circle that will represent a disk for the game
	 * @param fill		Color of the circle
	 * @param stroke	Border color of circle
	 */
	public Disks(Color fill, Color stroke) {
		circle = new Circle(CircleSize);
		circle.setFill(fill); 
		circle.setStroke(stroke);    
	}
	
	
	
	
	// End of set up
	
	// Methods
	
	/**
	 * @return The circle object / disk 
	 */
	public Circle getCircle() { 
		return this.circle;
	}
	
	/**
	 * Getter method that return the color of the disk
	 * @return Color of the disk
	 */
	public Paint getColor() {
		System.out.println("the color is " + this.circle.getFill());
		
		return this.circle.getFill();
		
		
	}
	
	public String getTeam() {
		if(this.circle.getFill() == Color.WHITE) {
			// white
			return "white";
		}
		else {// black
			return "black";
		}
	}
	
	// End of methods
}

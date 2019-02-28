package gui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Definition of Triangle class for pip UI
 * @author YeohB - 17357376
 * @author Ee En Goh - 1720691
 */

public class Triangle {

	/**
	 * Instance variables
	 */
	private Polygon polygon;
	private double x1 = 40.0;
	private double x2 = 80.0;
	private double x3 = 60.0;
	private double y = 0.0;
	private double height = 250.0;
	
	/**
	 * Constructor that creates a polygon that will act as pip object for the game
	 * 
	 * @param color
	 */
	Triangle(Color color) {
		polygon = new Polygon();
		polygon.getPoints().addAll(new Double[] { x1 , y, x2 , y, x3, height});
		
		changeColor(color);
	}
	
	/**
	 * 
	 * @return the polygon object
	 */
	public Polygon getPolygon() {
		return this.polygon;
	}
	
	/**
	 * Providing setFill method a more understandable name
	 * 
	 * @param color changes the color of the shape
	 */
	private void changeColor(Color color) {
		polygon.setFill(color);
	}
}

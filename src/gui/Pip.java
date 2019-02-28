package gui;

import data_structures.LinkedStack;
import javafx.scene.paint.Color;

/**
 * Pip class 
 * @author YeohB - 17357376
 * @author Ee En Goh - 17202691
 */

public class Pip extends LinkedStack<Disks> {

	/* Instance variables */
	
	/** Index number of this pip ( 0 - 23 ) */
	private int pipNum;
	
	/** Number of disks on this pip */
	private int diskCount;
	
	/** The color of disk(s) on the current pip */
	// private Color diskColorOnPip;
	
	private Triangle triangle;
	
	/**
	 * Constructor to create a pip object to represent the individual points where
	 * the disks reside
	 * @param num The given pip index, which also indicates where this pip should be inserted
	 */
	public Pip(int num) {
		triangle = new Triangle(Color.WHITE);
		pipNum = num;
		diskCount = 0;
		// diskColorOnPip = null;
	}

	/**
	 * Construct pips - Each pip is visually a stackpane, and logically a stack
	 * @param color			The color of this pip
	 * @param orientation	The degree of rotation of this pip, depending on the pip index
	 */
	public void initPip(Color color, double orientation) {
		triangle.getPolygon().setFill(color);
		setRotate(orientation); 
		getChildren().add(triangle.getPolygon());
	}

	/**
	 * Insert disks into the pip stack
	 * @param diskCount	The original number of disks on this pip
	 * @param color		The color of disk that inserted into given pip
	 * @param stroke	The border color of the inserted disk
	 */
	public void initDisks(int diskCount, Color color, Color stroke) {
		
		for (int i = 0; i < diskCount; i++) {
			push(new Disks(color, stroke)); // Game Logic
			this.diskCount = diskCount;
		}
		
		updateDisks();
	}

	/**
	 * Update the details of disks on this pip
	 */
	public void updateDisks() {
		
		int i = diskCount - 1;
		
		if(size() >= 5){
			for(Disks disk : this) {
				disk.getCircle().setTranslateY(-100 + (i-- * 30));
				getChildren().add(disk.getCircle());
			}
		}
		else{
			for(Disks disk : this){
				disk.getCircle().setTranslateY(-100 + (i-- * 40));
				getChildren().add(disk.getCircle());
			}
		}			
	}
	
	/**
	 * Method to withdraw disks from pip ( popped from stack )
	 * @return The removed disk, a Disk object 
	 */
	public Disks updatePoppedDisks() {
		
		diskCount--;
		// Debugging purposes
		// System.out.println("Popped Disk number: " + top().getDiskNumber());
			
		getChildren().remove(top().getCircle());
		return pop();
	}
	
	/**
	 * Method to insert disks into pip ( pushed onto stack ) 
	 * @param disk	Disks object, the pushed disk onto this pip
	 */
	public void updatePushedDisks(Disks disk) {	 
			
		diskCount++;
		
		double size = 0;
		
		if(isEmpty()) {			
			size = -100;
		}else if(size() >= 5)
			size = top().getCircle().getTranslateY() + 30.0;
		else 
			size = top().getCircle().getTranslateY() + 40.0;
		
		push(disk);
		
		// Debugging purposes
		// System.out.println("Top(): " + top().getDiskNumber());
		
		top().getCircle().setTranslateY(size);
		getChildren().add(top().getCircle());
	}

	/**
	 * @return The pip number on this pip
	 */
	public int getPipNum() {
		return this.pipNum;
	}
	
	// ----- DiskCount Calculation -----
	// TODO no needed
	public void increaseDiskCount() {
		this.diskCount++;
	}
	
	/**
	 * @return The color of disk (s) on any pip (stack, look for the top element)
	 */
	public String returnDiskColor() {
		String color;

		if (top().getColor() == Color.BLACK) {
			color = "black";
		} else {
			color = "white";
		}

		return color;
	}
	
	/**
	 * Boolean method to check if the disk in this pip can be hit by opponent's disk
	 * @return	True if there is only 1 disk on the pip, else false
	 */
/*	public boolean validDiskHit() {	// TODO
		
		if(diskCount == 1) 
			return true;
		return false;
	} */
}

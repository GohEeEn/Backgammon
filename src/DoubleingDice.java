import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

/**
 * Doubling Dice class that define the GUI ofit
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class DoubleingDice extends JComponent {
	
	private static final long serialVersionUID = 1L;

	// Constants
	private static final int[] diceSides = {1,2,4,8,16,32,64};
	
	// Variables
	// Display
	private Ellipse2D.Double circle;
	private JTextField theNumberOnDice; 
	// End of display
	private static int diceSide_Pointer;
	
	// Set up
	public DoubleingDice() {
		this.setPreferredSize(new Dimension(100,100));
		
		JTextField doubleDiceHeading = new JTextField();
		doubleDiceHeading.setOpaque(false);
		doubleDiceHeading.setBounds(0, 0, 100, 15);
		doubleDiceHeading.setEnabled(false);
		doubleDiceHeading.setText("doubling cube");

		theNumberOnDice = new JTextField();
		theNumberOnDice.setBounds(25, 25, 50, 50);
		theNumberOnDice.setEnabled(false);
		//theNumberOnDice.setBackground(Color.black);
		
		this.add(doubleDiceHeading);
		this.add(theNumberOnDice);
		//theNumberOnDice.setSize(100,100);
		
		diceSide_Pointer = 0;
		updateDiceDisplay();
	}
	
	public void updateDiceDisplay() {
		theNumberOnDice.setText("" + diceSides[diceSide_Pointer]);
	}
	
	// End of set up
	
	// Methods
	
	public void doubleTheCube() {
		// Chang the dice side by increaseing pojter
		diceSide_Pointer++;
		
		
		// Change the dice graphic
	}
	
	
	
	// Graphics
	//x and y are the coordinates on the board to draw the piece
		@Override
		public void paintComponent(Graphics g)
		{
			// Paint the piece and fill it
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D) g;
			
			circle = new Ellipse2D.Double(0, 0,100, 100);
		    
		    g2.setColor(Color.blue);
			
		    g2.fill(circle);
		    g2.draw(circle);
			
		}
	
	
	
	
}

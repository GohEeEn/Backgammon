import java.awt.*;

/**
 * Player class that holds the details for one player
 * @author Ee En Goh 		17202691
 * @author Ferdia Fagan 	16372803
 */
public class Player implements PlayerAPI{ 

    private int id;
    private String colorName;
    private Color color;
    private String name;
    private Dice dice;
    private int match_score;

    Player(int id, String colorName, Color color) {
        this.id = id;
        name = "";
        this.colorName = colorName;
        this.color = color;
        dice = new Dice();
        this.match_score = 0;
    }

    Player(Player player) {
        id = player.id;
        colorName = player.colorName;
        color = player.color;
        name = player.name;
        dice = new Dice(player.dice);
        this.match_score = 0;
    }

    // ----- GETTER and SETTER METHODS -----
    
    public int getId() { return id; }

    public String getColorName() { return this.colorName; }

    public Color getColor() { return this.color; }

    public Dice getDice() { return dice; }
    
    public int getScore() { return this.match_score; }
    
    public void setName(String name) { this.name = name; }
    
    public void addScore(int score) { this.match_score += score; }
    
    // ----- END OF GETTER and SETTER METHODS -----
    
    public String toString() { return this.name; }
    
    /**
     * Clear the player information in order to restart a new match for new player
     */
    public void reset() {
    	name = "";
    	match_score = 0;
    }
    
}

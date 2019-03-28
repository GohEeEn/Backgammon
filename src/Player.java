import java.awt.*;

public class Player {
    // Player holds the details for one player

    private int id;
    private String colorName;
    private Color color;
    private String name;
    private Dice dice;

    Player(int id, String colorName, Color color) {
        this.id = id;
        name = "";
        this.colorName = colorName;
        this.color = color;
        dice = new Dice();
    }

    Player(Player player) {
        id = player.id;
        colorName = player.colorName;
        color = player.color;
        name = player.name;
        dice = new Dice(player.dice);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getColorName() {
        return this.colorName;
    }

    public Color getColor() {
        return this.color;
    }

    public Dice getDice() { return dice; }

    public String toString() {
        return name;
    }
}

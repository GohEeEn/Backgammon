/**
 * Game Bot Class
 * @author Ee En Goh
 *
 */
public class Francis implements BotAPI {

    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;

    Francis(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
    }

    /**
     * return the bot name (must match the class name)
     */
    public String getName() {
        return "Francis"; 
    }

    /**
     * TODO
     */
    public String getCommand(Plays possiblePlays) {
        // Add your code here
        return "1";
    }

    /**
     * TODO
     */
    public String getDoubleDecision() {
        // Add your code here
        return "n";
    }
}

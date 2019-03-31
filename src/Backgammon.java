import java.util.concurrent.TimeUnit;

public class Backgammon {
    // This is the main class for the Backgammon game. It orchestrates the running of the game.

    public static final int NUM_PLAYERS = 2;

    private final Players players = new Players();
    private final Board board = new Board(players);
    private final UI ui = new UI(board,players);

    private void getPlayerNames() {
        for (Player player : players) {
            ui.promptPlayerName();
            String name = ui.getString();
            ui.displayString("> " + name);
            player.setName(name);
            ui.displayPlayerColor(player);
        }
    }

    private void rollToStart() {
        do {
            for (Player player : players) {
                player.getDice().rollDie();
                ui.displayRoll(player);
            }
            if (players.isEqualDice()) {
                ui.displayDiceEqual();
            }
        } while (players.isEqualDice());
        players.setCurrentAccordingToDieRoll();
        ui.displayDiceWinner(players.getCurrent());
        ui.display();
    }

    private void takeTurns() throws InterruptedException {
        Command command = new Command();
        boolean firstMove = true;
        do {
            Player currentPlayer = players.getCurrent();
            Dice currentDice;
            if (firstMove) {
                currentDice = new Dice(players.get(0).getDice().getDie(),players.get(1).getDice().getDie());
                firstMove = false;
            } else {
                currentPlayer.getDice().rollDice();
                ui.displayRoll(currentPlayer);
                currentDice = currentPlayer.getDice();
            }
            Plays possiblePlays;
            possiblePlays = board.getPossiblePlays(currentPlayer,currentDice);
            if (possiblePlays.number()==0) {
                ui.displayNoMove(currentPlayer);
            } else if (possiblePlays.number()==1) {
                ui.displayForcedMove(currentPlayer);
                board.move(currentPlayer, possiblePlays.get(0));
            } else {
                ui.displayPlays(currentPlayer, possiblePlays);
                ui.promptCommand(currentPlayer);
                command = ui.getCommand(possiblePlays);
                ui.displayString("> " + command);
                if (command.isMove()) {
                    board.move(currentPlayer, command.getPlay());
                } else if (command.isCheat()) {
                    board.cheat();
                }
            }
            ui.display();
            TimeUnit.SECONDS.sleep(2);
            players.advanceCurrentPlayer();
            ui.display();
        } while (!command.isQuit() && !board.isGameOver());
    }

    private void play() throws InterruptedException {
//        board.setUI(ui);
        ui.display();
        ui.displayStartOfGame();
        getPlayerNames();
        rollToStart();
        takeTurns();
        if (board.isGameOver()) {
            ui.displayGameWinner(board.getWinner());
            TimeUnit.SECONDS.sleep(10);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Backgammon game = new Backgammon();
        game.play();
        System.exit(0);
    }
}

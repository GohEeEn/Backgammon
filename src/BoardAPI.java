public interface BoardAPI {

    int[][] get();

    /**
     * Method to get the number of the given player's checkers at the given pip
     * @param player	The given player
     * @param pip		The given pip index
     * @return			The number of current player's checkers at taht given pip
     */
    int getNumCheckers(int player, int pip);
    
    /**
     * Method to get the total number of checkers at the given pip index
     * @param pipPosition	The given pip index
     * @return				Total number of checkers at the given pip index
     */
    int getNumbCheckersAtPosition(int pipPosition);

    /**
     * Method to get any possible play (combination of checker's move), based on the current board
     * @param playerId	Player ID of the current player
     * @param dice		Dice roll value in current game turn
     * @return			An ArrayList
     */
    Plays getPossiblePlays(int playerId, Dice dice);

    boolean lastCheckerInInnerBoard(int playerId);

    boolean lastCheckerInOpponentsInnerBoard(int playerId);

    boolean allCheckersOff(int playerId);

    boolean hasCheckerOff(int playerId);
}


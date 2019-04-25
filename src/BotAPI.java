public interface BotAPI {

    String getName();

    /**
     * Method to get a game play from bot 
     * @param possiblePlays
     * @return	Currently an int which demonstrate the index of play on the move list
     */
    String getCommand(Plays possiblePlays);

    /**
     * Method to decide if this bot wants to accept the doubling cube challenge offered by the opponent
     * @return String, "double" if it wants to, else "no"
     */
    String getDoubleDecision();
    
    Double[] getWeights();	// Uncomment so as to work with training the bot

    void swapWeightsWithOtherPlayer(Double[] botWeights);		// Uncomment so as to work with training the bot

    void saveWeights();	// Uncomment so as to work with training the bot
    void retrieveWeights();	// Uncomment so as to work with training the bot

    void botWins();	// Uncomment so as to work with training the bot
    void botLoses();	// Uncomment so as to work with training the bot
}

public interface BotAPI {

    String getName();

    String getCommand(Plays possiblePlays);

    String getDoubleDecision();
    
    Double[] getWeights();	// Uncomment so as to work with training the bot
    
    void swapWeightsWithOtherPlayer(Double[] botWeights);		// Uncomment so as to work with training the bot
    
    void saveWeights();	// Uncomment so as to work with training the bot
    void retrieveWeights();	// Uncomment so as to work with training the bot
    
    void botWins();	// Uncomment so as to work with training the bot
    void botLoses();	// Uncomment so as to work with training the bot
    
}

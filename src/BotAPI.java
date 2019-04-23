public interface BotAPI {

    String getName();

    String getCommand(Plays possiblePlays);

    String getDoubleDecision();
    
    int[] getWeights();
    
    void swapWeightsWithOtherPlayer(int[] botWeights);
}

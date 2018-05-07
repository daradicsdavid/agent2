package application.exception;

public class BadAgentNumberGuessException extends Exception {
    private final int agentNumberGuess;

    public BadAgentNumberGuessException(int agentNumberGuess) {

        this.agentNumberGuess = agentNumberGuess;
    }

    public int getAgentNumberGuess() {
        return agentNumberGuess;
    }
}

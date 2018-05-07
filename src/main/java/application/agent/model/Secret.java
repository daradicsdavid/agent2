package application.agent.model;

public class Secret {
    private final String secretWord;
    private boolean exposed = false;

    Secret(String secretWord) {
        this.secretWord = secretWord;
    }

    public String getSecretWord() {
        return secretWord;
    }

    @Override
    public String toString() {
        return "Secret{" +
                "secretWord='" + secretWord + '\'' +
                ", exposed=" + exposed +
                '}';
    }

    boolean isExposed() {
        return exposed;
    }

    void setExposed() {
        this.exposed = true;
    }
}

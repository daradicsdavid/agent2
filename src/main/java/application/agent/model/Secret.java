package application.agent.model;

public class Secret {
    private final String secret;
    private boolean exposed;

    public Secret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "Secret{" +
                "secret='" + secret + '\'' +
                ", exposed=" + exposed +
                '}';
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }
}

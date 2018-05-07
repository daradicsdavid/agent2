package application.file;

import java.util.List;

public class AgentFileData {
    private final List<String> aliases;
    private final String secret;

    AgentFileData(List<String> aliases, String secret) {
        this.aliases = aliases;
        this.secret = secret;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getSecret() {
        return secret;
    }
}

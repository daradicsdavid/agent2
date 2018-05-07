package application.agent.model;

import application.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SecretRepository {

    private final List<Secret> secrets;

    SecretRepository() {
        secrets = new ArrayList<>();
    }

    void addSecret(String secretWord) {
        if (secrets.stream().noneMatch(secret -> secret.getSecretWord().equals(secretWord))) {
            secrets.add(new Secret(secretWord));
        }
    }

    @Override
    public String toString() {
        return "SecretRepository{" +
                "secrets=" + secrets +
                '}';
    }

    Secret getRandomSecret() {
        return RandomUtils.getRandomElement(secrets);
    }

    public Secret getRandomNotExposedSecret() {
        Secret randomSecret = getRandomSecret();
        while (randomSecret.isExposed()) {
            randomSecret = getRandomSecret();
        }
        randomSecret.setExposed();
        return randomSecret;
    }

    public boolean isAllSecretsExposed() {
        return secrets.stream().allMatch(Secret::isExposed);
    }

    public List<String> getAllSecrets() {
        return secrets.stream().map(Secret::getSecretWord).collect(Collectors.toList());
    }
}


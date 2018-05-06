package application.agent.model;

import application.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Secrets {

    private final List<Secret> secrets;

    public Secrets() {
        secrets = new ArrayList<>();
    }

    public void addSecret(String secretWord) {
        if (secrets.stream().noneMatch(secret -> secret.getSecret().equals(secretWord))) {
            secrets.add(new Secret(secretWord));
        }
    }

    @Override
    public String toString() {
        return "Secrets{" +
                "secrets=" + secrets +
                '}';
    }

    public Secret getRandomSecret() {
        return RandomUtils.getRandomElement(secrets);
    }

    public Secret getRandomNotExposedSecret() {
        Secret randomSecret = getRandomSecret();
        while (randomSecret.isExposed()) {
            randomSecret = getRandomSecret();
        }
        randomSecret.setExposed(true);
        return randomSecret;
    }

    public boolean isAllSecretsExposed() {
        return secrets.stream().allMatch(Secret::isExposed);
    }

    public List<String> getAllSecrets() {
        return secrets.stream().map(Secret::getSecret).collect(Collectors.toList());
    }
}


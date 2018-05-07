package application.agent.model;

import application.file.AgentFileData;
import application.util.RandomUtils;
import java.util.*;


public class Agent {

    private final int number;
    private final Agency agency;
    private final List<String> aliases;
    private final SecretRepository secretRepository;


    public Agent(AgentFileData agentFileData, Agency agency, int number) {
        this.number = number;
        this.agency = agency;
        this.aliases = agentFileData.getAliases();
        secretRepository = new SecretRepository();
        secretRepository.addSecret(agentFileData.getSecret());
    }

    @Override
    public String toString() {
        return "Agent{" +
                "number=" + number +
                ", agency=" + agency +
                ", aliases=" + aliases +
                ", secretRepository=" + secretRepository +
                '}';
    }

    public int getNumber() {
        return number;
    }

    public Agency getAgency() {
        return agency;
    }

    public String getRandomAlias() {
        int random = RandomUtils.getRandom(0, aliases.size() - 1);
        return aliases.get(random);
    }

    public String getName() {
        return "Agent " + agency + "-" + number;
    }


    public Secret getRandomSecret() {
        return secretRepository.getRandomSecret();
    }

    public void addSecret(String secretWord) {
        secretRepository.addSecret(secretWord);
    }

    public SecretRepository getSecretRepository() {
        return secretRepository;
    }


}

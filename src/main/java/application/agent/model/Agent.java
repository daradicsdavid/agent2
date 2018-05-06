package application.agent.model;

import application.AgentConfiguration;
import application.file.AgentFileData;
import application.util.RandomUtils;

import java.util.*;


public class Agent {

    private final AgentConfiguration agentConfiguration;
    private final int number;
    private final Agency agency;
    private final List<String> aliases;
    private final Secrets secrets;
    private volatile boolean arrested = false;



    public Agent(AgentConfiguration agentConfiguration, AgentFileData agentFileData, Agency agency, int number) {
        this.agentConfiguration = agentConfiguration;
        this.number = number;
        this.agency = agency;
        this.aliases = agentFileData.getAliases();
        secrets = new Secrets();
        secrets.addSecret(agentFileData.getSecret());
    }

    @Override
    public String toString() {
        return "Agent{" +
                "number=" + number +
                ", agency=" + agency +
                ", aliases=" + aliases +
                ", secrets=" + secrets +
                '}';
    }

    public boolean isNotArrested() {
        return !arrested;
    }

    public void setArrested() {
        arrested = true;
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
        return secrets.getRandomSecret();
    }

    public void addSecret(String secretWord) {
        secrets.addSecret(secretWord);
    }

    public Secrets getSecrets() {
        return secrets;
    }




}

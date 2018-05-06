package application.agent;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.model.Agency;
import application.agent.model.Agent;
import application.file.AgentFileData;
import application.file.AgentReader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AgentBuilder {


    private final AgentReader agentReader = new AgentReader();
    private final AgentConfiguration agentConfiguration;
    private final OutputWriter outputWriter = new OutputWriter("AgentBuilder");
    private final List<String> firstAgencySecrets = new ArrayList<>();
    private final List<String> secondAgencySecrets = new ArrayList<>();

    public AgentBuilder(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }

    public List<Agent> build() {
        outputWriter.print("Ügynökök beolvasása!");
        List<Agent> agents = new ArrayList<>();
        for (int i = 1; i <= agentConfiguration.getNumberOfFirstAgencyMembers(); i++) {
            Agent agent = createAgent(Agency.FIRST, i);
            firstAgencySecrets.addAll(agent.getSecrets().getAllSecrets());
            agents.add(agent);
        }
        for (int i = 1; i <= agentConfiguration.getNumberOfSecondAgencyMembers(); i++) {
            Agent agent = createAgent(Agency.SECOND, i);
            secondAgencySecrets.addAll(agent.getSecrets().getAllSecrets());
            agents.add(agent);
        }
        outputWriter.print("Ügynökök sikeresen beolvasva!");
        return agents;
    }

    private Agent createAgent(Agency agency, int agentNumber) {
        AgentFileData agentFileData = agentReader.readAgentDataFromFile(agency, agentNumber);
        Agent agent = new Agent(agentConfiguration, agentFileData, agency, agentNumber);

        outputWriter.print("Ügynök: %s", agent);

        return agent;
    }

    public List<String> getFirstAgencySecrets() {
        return firstAgencySecrets;
    }

    public List<String> getSecondAgencySecrets() {
        return secondAgencySecrets;
    }
}

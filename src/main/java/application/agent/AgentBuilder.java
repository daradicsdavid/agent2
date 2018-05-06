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


    public AgentBuilder(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }

    public List<Agent> build() {
        outputWriter.print("Ügynökök beolvasása!");
        List<Agent> agents = new ArrayList<>();
        for (int i = 1; i <= agentConfiguration.getNumberOfFirstAgencyMembers(); i++) {
            agents.add(createAgent(Agency.FIRST, i));
        }
        for (int i = 1; i <= agentConfiguration.getNumberOfSecondAgencyMembers(); i++) {
            agents.add(createAgent(Agency.SECOND, i));
        }
        outputWriter.print("Ügynökök sikeresen beolvasva!");
        return agents;
    }

    private Agent createAgent(Agency agency, int agentNumber) {
        AgentFileData agentFileData = agentReader.readAgentDataFromFile(agency, agentNumber);
        Agent agent = new Agent(agentConfiguration,agentFileData, agency, agentNumber);

        outputWriter.print("Ügynök: %s", agent);

        return agent;
    }


}

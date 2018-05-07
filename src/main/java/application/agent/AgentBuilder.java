package application.agent;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.model.Agency;
import application.agent.model.Agent;
import application.file.AgentFileData;
import application.file.AgentReader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class AgentBuilder {


    private final AgentReader agentReader = new AgentReader();
    private final AgentConfiguration agentConfiguration;
    private final OutputWriter outputWriter = new OutputWriter("AgentBuilder");
    private final Map<Agency, List<String>> agencySecrets = new EnumMap<>(Agency.class);

    public AgentBuilder(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
        agencySecrets.put(Agency.FIRST, new ArrayList<>());
        agencySecrets.put(Agency.SECOND, new ArrayList<>());
    }

    public List<Agent> build() {
        outputWriter.print("Ügynökök beolvasása!");
        List<Agent> agents = new ArrayList<>();
        agents.addAll(buildAgentsForAgency(Agency.FIRST));
        agents.addAll(buildAgentsForAgency(Agency.SECOND));

        outputWriter.print("Ügynökök sikeresen beolvasva!");
        return agents;
    }

    private List<Agent> buildAgentsForAgency(Agency agency) {
        return IntStream.rangeClosed(1, agentConfiguration.getNumberOfFirstAgencyMembers()).mapToObj(iterator -> buildAgentForAgency(agency, iterator)).collect(Collectors.toList());
    }

    private Agent buildAgentForAgency(Agency agency, int agentNumber) {
        Agent agent = createAgent(agency, agentNumber);
        addSecrets(agency, agent.getSecretRepository().getAllSecrets());
        return agent;
    }

    private void addSecrets(Agency agency, List<String> allSecrets) {
        agencySecrets.get(agency).addAll(allSecrets);
    }

    private Agent createAgent(Agency agency, int agentNumber) {
        AgentFileData agentFileData = agentReader.readAgentDataFromFile(agency, agentNumber);
        Agent agent = new Agent(agentFileData, agency, agentNumber);

        outputWriter.print("Ügynök: %s", agent);

        return agent;
    }

    public Map<Agency, List<String>> getAgencySecrets() {
        return agencySecrets;
    }
}

package application.agent.model;

import application.AgentConfiguration;
import application.util.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;

import static application.agent.model.Agency.FIRST;

public class KnownAgentRepository {


    private final Map<String, KnownAgent> knownAgents;
    private final AgentConfiguration agentConfiguration;

    public KnownAgentRepository(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
        knownAgents = Collections.synchronizedMap(new HashMap<>());
    }

    public KnownAgent getKnownAgent(String alias) {
        return knownAgents.get(alias);
    }

    public void putAgentToKnownAgents(String alias, Agency agency) {
        knownAgents.put(alias, new KnownAgent(agency));
    }

    public int getRandomOtherAgencyAgentNumber(String alias) {
        KnownAgent knownAgent = getKnownAgent(alias);
        Integer upperBound;
        if (FIRST.equals(knownAgent.getAgency())) {
            upperBound = agentConfiguration.getNumberOfSecondAgencyMembers();
        } else {
            upperBound = agentConfiguration.getNumberOfFirstAgencyMembers();
        }
        List<Integer> exceptionNumbers = new ArrayList<>();
        exceptionNumbers.addAll(getKnownAgentNumbers());
        exceptionNumbers.addAll(knownAgents.get(alias).getWrongNumbers());
        return RandomUtils.getRandom(1, upperBound, exceptionNumbers);
    }

    private List<Integer> getKnownAgentNumbers() {
        return knownAgents.values().stream()
                .filter(knownAgent -> Objects.nonNull(knownAgent.getNumber()))
                .map(KnownAgent::getNumber).collect(Collectors.toList());
    }

}

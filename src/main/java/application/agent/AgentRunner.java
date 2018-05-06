package application.agent;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.client.AgentClient;
import application.agent.model.Agent;
import application.agent.server.AgentServer;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AgentRunner {

    private final AgentClient agentClient;
    private final AgentServer agentServer;
    private Agent agent;
    private ExecutorService agentExecutor;
    private final OutputWriter outputWriter;

    public AgentRunner(Agent agent, AgentConfiguration agentConfiguration) {
        this.agent = agent;
        UUID agentIdentifier = UUID.randomUUID();
        agentServer = new AgentServer(agent, agentConfiguration, agentIdentifier);
        agentClient = new AgentClient(agent, agentConfiguration, agentIdentifier);
        outputWriter = new OutputWriter(agent.getName() + " OutputWriter");
    }


    public void start() {
        outputWriter.print("Agent indul.", agent.getAgency(), String.valueOf(agent.getNumber()));
        agentExecutor = Executors.newFixedThreadPool(2);
        List<Callable<Object>> tasks = new ArrayList<>();
        tasks.add(agentServer);
        tasks.add(agentClient);
        try {
            agentExecutor.invokeAll(tasks);
            outputWriter.print("Az %s-%s ügynök leállt mivel le lett tartóztatva.", agent.getAgency(), agent.getNumber());
        } catch (InterruptedException e) {
        }
    }
}

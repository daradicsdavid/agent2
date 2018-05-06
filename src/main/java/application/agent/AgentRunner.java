package application.agent;

import application.AgentConfiguration;
import application.Application;
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


public class AgentRunner extends Thread {

    private final AgentClient agentClient;
    private final AgentServer agentServer;
    private Agent agent;
    private final Application application;
    private final OutputWriter outputWriter;
    private volatile boolean running = true;

    public AgentRunner(Agent agent, AgentConfiguration agentConfiguration, Application application) {
        this.agent = agent;
        this.application = application;
        UUID agentIdentifier = UUID.randomUUID();
        agentServer = new AgentServer(agent, agentConfiguration, agentIdentifier);
        agentClient = new AgentClient(agent, agentConfiguration, agentIdentifier, application);
        outputWriter = new OutputWriter(agent.getName() + " OutputWriter");
    }


    @Override
    public void run() {
        outputWriter.print("Agent indul.", agent.getAgency(), String.valueOf(agent.getNumber()));

        agentServer.start();
        agentClient.start();

        try {
            agentServer.join();
            agentClient.interrupt();
        } catch (InterruptedException e) {
            agentServer.interrupt();
            agentClient.interrupt();
        }
        running = false;
        application.notifyAboutStopWhenArrested(agent.getAgency());
    }

    public Agent getAgent() {
        return agent;
    }

    public boolean isRunning() {
        return running;
    }
}

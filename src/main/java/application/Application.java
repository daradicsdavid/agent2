package application;

import application.agent.model.Agency;
import application.agent.model.Agent;
import application.agent.AgentBuilder;
import application.agent.AgentRunner;
import application.util.NumberUtils;

import java.util.List;
import java.util.stream.Collectors;

import static application.agent.model.Agency.FIRST;
import static application.agent.model.Agency.SECOND;

public class Application extends Thread {

    private final OutputWriter outputWriter = new OutputWriter("Application");
    private final AgentConfiguration agentConfiguration;
    private List<AgentRunner> agents;
    private volatile boolean running = true;
    private final AgentBuilder agentBuilder;
    private Agency losingAgency;

    public static void main(String[] args) {
        try {
            Application application = new Application(args);
            application.start();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private Application(String[] args) {
        outputWriter.print("Alkalmazás indul.");
        checkArgs(args);
        agentConfiguration = new AgentConfiguration(NumberUtils.toNumber(args[0]), NumberUtils.toNumber(args[1]), NumberUtils.toNumber(args[2]), NumberUtils.toNumber(args[3]));
        outputWriter.print("Bemenet ellenőrizve: %s", agentConfiguration);
        agentBuilder = new AgentBuilder(agentConfiguration);
        buildAgents();
    }

    @Override
    public void run() {
        agents.forEach(AgentRunner::start);


        agents.forEach(agentRunner -> {
            try {
                agentRunner.join();
            } catch (InterruptedException ignored) {

            }
        });
        try {
            sleep(500);
        } catch (InterruptedException e) {

        }
        outputWriter.print("A játék befejeződött.A(z) %s ügynökség minden tagját letartóztatták." +
                "A(z) %s ügynökség nyert.", losingAgency, FIRST.equals(losingAgency) ? SECOND : FIRST);

        System.exit(0);
    }

    private void buildAgents() {
        List<Agent> builtAgents = agentBuilder.build();
        agents = builtAgents.stream().map(builtAgent -> new AgentRunner(builtAgent, agentConfiguration, this)).collect(Collectors.toList());
    }


    private void checkArgs(String[] args) {
        outputWriter.print("Bemenet ellenőrzése!");
        if (args.length != 4) {
            outputWriter.print("Nem megfelelő a bemenetek száma!");
            throw new IllegalArgumentException();
        }
    }

    public synchronized void notifyAboutStopWhenArrested(Agency agency) {
        if (running) {
            boolean allAgentsOfAgencyArrested = agents.stream().noneMatch(agentRunner ->
                    agentRunner.getAgent().getAgency().equals(agency) && agentRunner.isRunning());
            if (allAgentsOfAgencyArrested) {
                stopGame(agency);
            }
            running = false;
        }
    }

    public synchronized void notifyAboutAcquiredSecretsOfOtherAgency(String name, Agency agency, List<String> secrets) {
        if (running) {
            switch (agency) {
                case FIRST:
                    if (agentBuilder.getFirstAgencySecrets().containsAll(secrets)) {
                        outputWriter.print("%s nevű ügynök megszerezte a másik ügynökség összes titkát!", name);
                        stopGame(agency);
                    }
                    break;
                case SECOND:
                    if (agentBuilder.getSecondAgencySecrets().containsAll(secrets)) {
                        outputWriter.print("%s nevű ügynök megszerezte a másik ügynökség összes titkát!", name);
                        stopGame(agency);
                    }
                    break;
            }
            running = false;
        }
    }

    private void stopGame(Agency losingAgency) {
        agents.forEach(Thread::interrupt);
        this.losingAgency = losingAgency;
    }
}

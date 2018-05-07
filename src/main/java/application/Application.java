package application;

import application.agent.model.Agency;
import application.agent.model.Agent;
import application.agent.AgentBuilder;
import application.agent.AgentRunner;
import application.agent.model.SecretRepository;
import application.util.NumberUtils;

import java.util.List;
import java.util.stream.Collectors;

import static application.agent.model.Agency.FIRST;
import static application.agent.model.Agency.SECOND;
import static java.lang.Thread.sleep;

public class Application {

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

    private void start() throws InterruptedException {
        agents.forEach(AgentRunner::start);


        for (AgentRunner agent : agents) {
            agent.join();
        }

        sleep(1000);
        outputWriter.print("===========================================================================================");
        printAgents();
        outputWriter.print("A játék befejeződött.A(z) %s ügynökség nyert.", FIRST.equals(losingAgency) ? SECOND : FIRST);

        System.exit(0);
    }

    private void printAgents() {
        for (AgentRunner agent : agents) {
            outputWriter.print("Agent %s", agent.getAgent().getName());
            SecretRepository allSecretRepository = agent.getAgent().getSecretRepository();
            outputWriter.print("Megszerzett saját ügynökség üzenetek: %s", allSecretRepository);
            List<String> otherAgencySecrets = agent.getAgentClient().getOtherAgencySecrets();
            outputWriter.print("Megszerzett másik ügynökség üzenetek: %s", otherAgencySecrets);
        }
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
                outputWriter.print("%s ügynökség minden tagját letartóztatták!", agency);
                stopGame(agency);
            }
        }
    }

    public synchronized void notifyAboutAcquiredSecretsOfOtherAgency(String name, Agency agency, List<String> secrets) {
        if (running) {
            List<String> agencySecrets = agentBuilder.getAgencySecrets().get(agency);
            if (secrets.containsAll(agencySecrets)) {
                outputWriter.print("%s nevű ügynök megszerezte a másik ügynökség összes titkát!", name);
                stopGame(agency);
            }

        }
    }

    private void stopGame(Agency losingAgency) {
        agents.forEach(Thread::interrupt);
        this.losingAgency = losingAgency;
        running = false;
    }
}

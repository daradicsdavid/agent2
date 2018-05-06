package application;

import application.agent.model.Agent;
import application.agent.AgentBuilder;
import application.agent.AgentRunner;
import application.util.NumberUtils;

import java.util.List;
import java.util.stream.Collectors;

public class Application {

    private final OutputWriter outputWriter = new OutputWriter("Application");
    private final AgentConfiguration agentConfiguration;
    private List<AgentRunner> agents;

    public static void main(String[] args) {
        try {
            Application application = new Application(args);
            application.start();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public Application(String[] args) {
        outputWriter.print("Alkalmazás indul.");
        checkArgs(args);
        agentConfiguration = new AgentConfiguration(NumberUtils.toNumber(args[0]), NumberUtils.toNumber(args[1]), NumberUtils.toNumber(args[2]), NumberUtils.toNumber(args[3]));
        outputWriter.print("Bemenet ellenőrizve: %s", agentConfiguration);
        buildAgents();
    }

    private void buildAgents() {
        AgentBuilder agentBuilder = new AgentBuilder(agentConfiguration);
        List<Agent> builtAgents = agentBuilder.build();
        agents = builtAgents.stream().map(builtAgent -> new AgentRunner(builtAgent, agentConfiguration)).collect(Collectors.toList());
    }

    private void start() {
        agents.forEach(AgentRunner::start);
    }

    private void checkArgs(String[] args) {
        outputWriter.print("Bemenet ellenőrzése!");
        if (args.length != 4) {
            outputWriter.print("Nem megfelelő a bemenetek száma!");
            throw new IllegalArgumentException();
        }
    }
}

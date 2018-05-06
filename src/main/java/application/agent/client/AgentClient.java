package application.agent.client;

import application.AgentConfiguration;
import application.Application;
import application.OutputWriter;
import application.agent.model.Agency;
import application.agent.model.Agent;
import application.agent.model.KnownAgent;
import application.agent.model.KnownAgents;
import application.exception.SamePortException;
import application.util.RandomUtils;
import application.util.ThreadUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import static application.constant.MessageConstants.NOT_OK;
import static application.constant.MessageConstants.OK;

public class AgentClient extends Thread {
    private final OutputWriter outputWriter;
    private final Application application;
    private final Agent agent;
    private final AgentConfiguration agentConfiguration;
    private final AgentTimer agentTimer;
    private final UUID agentIdentity;
    private final KnownAgents knownAgents;
    private final List<String> otherAgencySecrets;

    public AgentClient(Agent agent, AgentConfiguration agentConfiguration, UUID agentIdentity, Application application) {
        this.agent = agent;
        this.agentIdentity = agentIdentity;
        this.agentConfiguration = agentConfiguration;
        this.application = application;
        this.agentTimer = new AgentTimer(agentConfiguration.getLowerBoundaryOfWait(), agentConfiguration.getUpperBoundaryOfWait());
        knownAgents = new KnownAgents(agentConfiguration);
        outputWriter = new OutputWriter(agent.getName() + " AgentClient");
        otherAgencySecrets = new ArrayList<>();
    }


    @Override
    public void run() {
        agentTimer.startTimer();
        while (!isInterrupted()) {
            if (agentTimer.isTimeElapsed()) {
                communicateWithServer();
                agentTimer.resetTimer();
            } else {
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        outputWriter.print("Ügynök kliens leáll.");
    }

    private void communicateWithServer() {
        if (isInterrupted()) {
            return;
        }

        int port = RandomUtils.generatePort(agentConfiguration.getLowerPortBoundary(), agentConfiguration.getUpperPortBoundary());
        try (Socket socket = new Socket("localhost", port); Scanner in = new Scanner(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            verifyServer(in, out);

            String alias = receiveAlias(in);

            Agency agency = getAgencyBasedOnAlias(alias);
            sendAgencyAnswer(out, agency);

            if (!receivedAcknowledgement(in)) {
                return;
            }

            if (knownAgents.getKnownAgent(alias) == null) {
                saveAgentToKnownAgents(alias, agency);
            } else {
                outputWriter.print("%s már ismert ügynök!", alias);
            }

            if (agent.getAgency().equals(agency)) {
                handleSameAgency(in, out);
            } else {
                handleDifferentAgency(in, out, alias);
            }

        } catch (IOException | TimeoutException | SamePortException e) {
        }

    }

    private void verifyServer(Scanner in, PrintWriter out) throws SamePortException {
        out.println(agentIdentity);
        String response = in.nextLine();
        if (NOT_OK.equals(response)) {
            //outputWriter.print("A kliens el lett utasitva!");
            throw new SamePortException();
        } else {
            outputWriter.print("A kliens sikeresen csatlakozott!");
        }
    }


    private void handleSameAgency(Scanner in, PrintWriter out) {
        sendOkSignal(out);

        receiveSecret(in);
        sendSecret(out);
    }

    private void sendSecret(PrintWriter out) {
        String randomSecret = agent.getRandomSecret().getSecret();
        outputWriter.print("Random titok küldése a szervernek: %s.", randomSecret);
        out.println(randomSecret);
    }

    private void receiveSecret(Scanner in) {
        String randomSecretFromServer = in.nextLine();
        outputWriter.print("Titok fogadva a szervertől: %s.", randomSecretFromServer);
        agent.addSecret(randomSecretFromServer);
        outputWriter.print("Jelenlegi titkok:%s", agent.getSecrets());
    }

    private void sendOkSignal(PrintWriter out) {
        outputWriter.print("A szerver ugyanahhoz az ügynökséghez tartozik mint ez a kliens, OK küldése!");
        out.println(OK);
    }

    private void handleDifferentAgency(Scanner in, PrintWriter out, String alias) throws TimeoutException {
        outputWriter.print("A szerver nem ugyanahhoz az ügynökséghez tartozik mint ez a kliens, ??? küldése!");
        out.println(NOT_OK);


        int randomOtherAgencyAgentNumber = sendAgentNumberAnswer(out, alias);

        KnownAgent knownAgent = knownAgents.getKnownAgent(alias);
        try {
            String response = ThreadUtils.receiveResponseWithTimeOut(in, 500);
            knownAgent.setNumber(randomOtherAgencyAgentNumber);
            addOtherAgencySecret(knownAgent, response);
            outputWriter.print("A kliens kitalálta a szerver ügynök számát! Kapott titok:%s", response);
        } catch (TimeoutException e) {
            knownAgent.addWrongNumber(randomOtherAgencyAgentNumber);
            outputWriter.print("A kliens nem kitalálta a szerver ügynök számát!");
            throw e;
        }

    }

    private int sendAgentNumberAnswer(PrintWriter out, String alias) {
        int randomOtherAgencyAgentNumber = knownAgents.getRandomOtherAgencyAgentNumber(alias);
        out.println(randomOtherAgencyAgentNumber);
        outputWriter.print("Ügynök szám tipp küldése: %s", randomOtherAgencyAgentNumber);
        return randomOtherAgencyAgentNumber;
    }

    private void saveAgentToKnownAgents(String alias, Agency agency) {
        outputWriter.print("Ügynök mentése ismert ügynökök közé: Ügynökség - %s, Álnév - %s", agency, alias);
        knownAgents.putAgentToKnownAgents(alias, agency);
    }

    private boolean receivedAcknowledgement(Scanner in) {
        try {
            ThreadUtils.receiveResponseWithTimeOut(in, 300);
            outputWriter.print("Az elküldött ügynökség helyes volt!");
            return true;
        } catch (TimeoutException e) {
            outputWriter.print("Az elküldött ügynökség helytelen volt!");
        }

        return false;
    }

    private Agency getAgencyBasedOnAlias(String alias) {
        KnownAgent knownAgent = knownAgents.getKnownAgent(alias);
        if (knownAgent != null) {
            Agency knownAgentAgency = knownAgent.getAgency();
            outputWriter.print("Kapott álnév az %s ügynökséghez tartozik.", knownAgentAgency);
            return knownAgentAgency;
        }

        outputWriter.print("Kapott álnév nem tartozik ismert ügynökhöz!");
        return guessAgency();
    }

    private void sendAgencyAnswer(PrintWriter out, Agency agencyOfAgent) {
        out.println(agencyOfAgent);
    }

    private Agency guessAgency() {
        Agency agencyByNumber = Agency.getAgencyByNumber(RandomUtils.getRandom(1, 2));
        outputWriter.print("Tippelt ügynökség küldése: %s", agencyByNumber);
        return agencyByNumber;
    }

    private String receiveAlias(Scanner in) throws TimeoutException {
        String alias = ThreadUtils.receiveResponseWithTimeOut(in, 500);
        outputWriter.print("Kapott álnév: %s", alias);
        return alias;
    }

    public void addOtherAgencySecret(KnownAgent knownAgent, String response) {
        otherAgencySecrets.add(response);
        application.notifyAboutAcquiredSecretsOfOtherAgency(agent.getName(), knownAgent.getAgency(), otherAgencySecrets);
    }
}

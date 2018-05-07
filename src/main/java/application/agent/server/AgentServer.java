package application.agent.server;

import application.AgentConfiguration;
import application.OutputWriter;
import application.agent.model.Agency;
import application.agent.model.Agent;
import application.agent.model.Secret;
import application.exception.AllSecretsExposedException;
import application.exception.BadAgentNumberGuessException;
import application.exception.SamePortException;
import application.util.NumberUtils;
import application.util.RandomUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

import static application.constant.MessageConstants.NOT_OK;
import static application.constant.MessageConstants.OK;

public class AgentServer extends Thread {

    private final OutputWriter outputWriter;
    private final Agent agent;
    private final AgentConfiguration agentConfiguration;
    private final UUID agentIdentity;


    public AgentServer(Agent agent, AgentConfiguration agentConfiguration, UUID agentIdentity) {
        this.agent = agent;
        this.agentIdentity = agentIdentity;
        this.agentConfiguration = agentConfiguration;
        outputWriter = new OutputWriter(agent.getName() + " AgentServer");
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                handleClient();
            } catch (AllSecretsExposedException e) {
                outputWriter.print("Az ügynök le lett tartóztatva, minden általa ismert titkot elárult.");
                interrupt();
            }
        }
        outputWriter.print("Ügynök szerver leáll.");
    }


    private void handleClient() throws AllSecretsExposedException {
        Integer currentPort = RandomUtils.generatePort(AgentConfiguration.LOWER_PORT_BOUNDARY, AgentConfiguration.UPPER_PORT_BOUNDARY);
        try (ServerSocket serverSocket = new ServerSocket(currentPort)) {
            serverSocket.setSoTimeout(agentConfiguration.getUpperBoundaryOfWait());

            acceptClient(serverSocket);
        } catch (IOException ignored) {
            outputWriter.print("Nem sikerült portot nyitni.");
        }
    }

    private void acceptClient(ServerSocket serverSocket) throws AllSecretsExposedException, IOException {
        try (Socket clientSocket = serverSocket.accept();
             Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            verifyClient(in, out);

            sendRandomAlias(out);
            Agency agencyGuess = receiveAgencyGuess(in);
            if (!agent.getAgency().equals(agencyGuess)) {
                outputWriter.print("Helytelen ügynökség tipp: %s. A kapcsolat bontásra kerül!", agencyGuess);
                return;
            }

            sendOkSignal(out);

            String response = receiveResponse(in);

            if (response.equals(OK)) {
                handleSameAgency(in, out);
            } else if (response.equals(NOT_OK)) {
                handleDifferentAgency(in, out);
            }

        } catch (SamePortException ignored) {
            outputWriter.print("A kliens ugyanaz az ügynök mint a szerver! A kapcsolat bontásra kerül.");
        } catch (BadAgentNumberGuessException e) {
            outputWriter.print("Helytelen ügynök szám tipp: %s. A kapcsolat bontásra kerül!", e.getAgentNumberGuess());
        }
    }

    private void handleDifferentAgency(Scanner in, PrintWriter out) throws BadAgentNumberGuessException, AllSecretsExposedException {
        int agentNumberGuess = receiveAgentNumberGuess(in);
        if (agent.getNumber() != agentNumberGuess) {
            throw new BadAgentNumberGuessException(agentNumberGuess);
        } else {
            sendRandomSecret(out);
        }
    }

    private void sendRandomSecret(PrintWriter out) throws AllSecretsExposedException {
        Secret randomNotExposedSecret = agent.getSecretRepository().getRandomNotExposedSecret();
        out.println(randomNotExposedSecret.getSecretWord());
        outputWriter.print("Random titok küldése: %s", randomNotExposedSecret.getSecretWord());
        if (agent.getSecretRepository().isAllSecretsExposed()) {
            throw new AllSecretsExposedException();
        }
    }

    private int receiveAgentNumberGuess(Scanner in) {
        return NumberUtils.toNumber(in.nextLine());
    }

    private void verifyClient(Scanner in, PrintWriter out) throws SamePortException {
        UUID clientIdentity = UUID.fromString(in.nextLine());
        if (!agentIdentity.equals(clientIdentity)) {
            out.println(OK);
            outputWriter.print("Egy  kliens sikeresen csatlakozott!");
        } else {
            out.println(NOT_OK);
            throw new SamePortException();
        }
    }

    private void handleSameAgency(Scanner in, PrintWriter out) {
        outputWriter.print("A kliens is ugyanahhoz az ügynökséghez tartozik mint ez a szerver!");
        sendSecret(out);
        receiveSecret(in);
    }

    private void receiveSecret(Scanner in) {
        String randomSecretFromClient = in.nextLine();
        outputWriter.print("Titok fogadva a klienstől: %s.", randomSecretFromClient);
        agent.addSecret(randomSecretFromClient);
        outputWriter.print("Jelenlegi titkok:%s", agent.getSecretRepository());
    }

    private void sendSecret(PrintWriter out) {
        String randomSecret = agent.getRandomSecret().getSecretWord();
        outputWriter.print("Random titok küldése a kliensnek: %s.", randomSecret);
        out.println(randomSecret);
    }

    private String receiveResponse(Scanner in) {
        String response = in.nextLine();
        outputWriter.print("Kapott üzenet: %s", response);
        return response;
    }

    private void sendOkSignal(PrintWriter out) {
        outputWriter.print("Helyes ügynökség tipp, OK küldése!");
        out.println(OK);
    }

    private Agency receiveAgencyGuess(Scanner in) {
        try {
            String agencyGuess = in.nextLine();
            int agencyNumber = NumberUtils.toNumber(agencyGuess);
            Agency agency = Agency.getAgencyByNumber(agencyNumber);
            outputWriter.print("Kapott ügynökség tipp: %s", agency);
            return agency;
        } catch (Exception e) {
            return null;
        }

    }

    private void sendRandomAlias(PrintWriter out) {
        String randomAlias = agent.getRandomAlias();
        outputWriter.print("Álnév küldése: %s", randomAlias);
        out.println(randomAlias);
    }


}

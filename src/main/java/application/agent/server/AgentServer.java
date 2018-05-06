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
import java.util.concurrent.Callable;

import static application.constant.MessageConstants.NOT_OK;
import static application.constant.MessageConstants.OK;

public class AgentServer implements Callable {

    private final OutputWriter outputWriter;
    private final Agent agent;
    private final AgentConfiguration agentConfiguration;
    private final UUID agentIdentity;
    private volatile Integer currentPort;
    private final Object portLock = new Object();

    public AgentServer(Agent agent, AgentConfiguration agentConfiguration, UUID agentIdentity) {
        this.agent = agent;
        this.agentIdentity = agentIdentity;
        this.agentConfiguration = agentConfiguration;
        outputWriter = new OutputWriter(agent.getName() + " AgentServer");
    }

    @Override
    public Object call() {
        while (agent.isNotArrested()) {
            ServerSocket serverSocket = openServerSocket();
            try {
                acceptRequest(serverSocket);
            } catch (AllSecretsExposedException e) {
                agent.setArrested();
                outputWriter.print("Az ügynök le lett tartóztatva, minden általa ismert titkot elárult.");
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private void acceptRequest(ServerSocket serverSocket) throws AllSecretsExposedException {
        try {
            Socket clientSocket = serverSocket.accept();
            handleClient(clientSocket);
            serverSocket.close();
        } catch (IOException e) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void handleClient(Socket clientSocket) throws AllSecretsExposedException {
        try (Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
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

        } catch (IOException | SamePortException | BadAgentNumberGuessException e) {

        }


    }

    private void handleDifferentAgency(Scanner in, PrintWriter out) throws BadAgentNumberGuessException, AllSecretsExposedException {
        int agentNumberGuess = receiveAgentNumberGuess(in);
        if (agent.getNumber() != agentNumberGuess) {
            outputWriter.print("Helytelen ügynök szám tipp: %s. A kapcsolat bontásra kerül!", agentNumberGuess);
            throw new BadAgentNumberGuessException();
        } else {
            sendRandomSecret(out);
        }
    }

    private void sendRandomSecret(PrintWriter out) throws AllSecretsExposedException {
        Secret randomNotExposedSecret = agent.getSecrets().getRandomNotExposedSecret();
        out.println(randomNotExposedSecret.getSecret());
        outputWriter.print("Random titok küldése: %s", randomNotExposedSecret.getSecret());
        if (agent.getSecrets().isAllSecretsExposed()) {
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
            //outputWriter.print("A csatlakozott kliens elutastitva mivel megegyezik a szerverrel!");
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
        outputWriter.print("Jelenlegi titkok:%s", agent.getSecrets());
    }

    private void sendSecret(PrintWriter out) {
        String randomSecret = agent.getRandomSecret().getSecret();
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

    private ServerSocket openServerSocket() {
        synchronized (portLock) {
            currentPort = null;
            ServerSocket serverSocket = null;
            while (getCurrentPort() == null) {
                try {
                    currentPort = RandomUtils.generatePort(agentConfiguration.getLowerPortBoundary(), agentConfiguration.getUpperPortBoundary());
                    serverSocket = new ServerSocket(getCurrentPort());
                    serverSocket.setSoTimeout(agentConfiguration.getUpperBoundaryOfWait());
                } catch (IOException e) {
                    currentPort = null;
                }
            }
            return serverSocket;
        }
    }


    public synchronized Integer getCurrentPort() {
        synchronized (portLock) {
            return currentPort;
        }
    }

}

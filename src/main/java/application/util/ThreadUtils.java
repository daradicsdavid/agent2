package application.util;

import java.util.Scanner;
import java.util.concurrent.*;

public class ThreadUtils {

    private ThreadUtils() {
    }

    public static String receiveResponseWithTimeOut(Scanner scanner, Integer timeOutInMillis) throws TimeoutException {
        FutureTask<String> readNextLine = new FutureTask<>(scanner::nextLine);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(readNextLine);

        try {
            return readNextLine.get(timeOutInMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new TimeoutException();
        }
    }

    public static String receiveResponseWithTimeOut(Scanner scanner) throws TimeoutException {
        return receiveResponseWithTimeOut(scanner, 500);
    }

}

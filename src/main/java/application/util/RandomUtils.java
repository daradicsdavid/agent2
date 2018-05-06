package application.util;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static int generatePort(Integer lowerPortBundary, Integer upperPortBundary, Integer exceptionPort) {
        int random = getRandom(lowerPortBundary, upperPortBundary);
        while (random == exceptionPort) {
            random = getRandom(lowerPortBundary, upperPortBundary);
        }
        return random;
    }

    public static int generatePort(Integer lowerPortBundary, Integer upperPortBundary) {
        return generatePort(lowerPortBundary, upperPortBundary, lowerPortBundary - 1);
    }

    public static int getRandom(int lowerBound, int upperBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
    }

    public static int getRandom(int lowerBound, int upperBound, List<Integer> exceptions) {
        int random = ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
        while (exceptions.contains(random)) {
            random = ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
        }
        return random;
    }

    public static <T> T getRandomElement(List<T> list) {
        int random = getRandom(0, list.size() - 1);
        return list.get(random);
    }


}

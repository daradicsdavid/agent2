package application.agent.client;

import application.util.RandomUtils;

import java.util.Random;

public class AgentTimer {
    private final int lowerBoundaryOfWait;
    private final int upperBoundaryOfWait;
    private long nextTickTime;

    public AgentTimer(int lowerBoundaryOfWait, int upperBoundaryOfWait) {
        this.lowerBoundaryOfWait = lowerBoundaryOfWait;
        this.upperBoundaryOfWait = upperBoundaryOfWait;
    }

    public void startTimer() {
        calculateNextTickTime();
    }

    public void resetTimer() {
        calculateNextTickTime();
    }

    public boolean isTimeElapsed() {
        return nextTickTime - System.currentTimeMillis() <= 0;
    }

    private void calculateNextTickTime() {
        int waitTime = RandomUtils.getRandom(lowerBoundaryOfWait, upperBoundaryOfWait);
        long currentTimeMillis = System.currentTimeMillis();
        nextTickTime = currentTimeMillis + waitTime;
    }


}

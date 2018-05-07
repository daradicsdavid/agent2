package application.agent.client;

import application.util.RandomUtils;

class AgentTimer {
    private final int lowerBoundaryOfWait;
    private final int upperBoundaryOfWait;
    private long nextTickTime;

    AgentTimer(int lowerBoundaryOfWait, int upperBoundaryOfWait) {
        this.lowerBoundaryOfWait = lowerBoundaryOfWait;
        this.upperBoundaryOfWait = upperBoundaryOfWait;
    }

    void startTimer() {
        calculateNextTickTime();
    }

    void resetTimer() {
        calculateNextTickTime();
    }

    boolean isTimeElapsed() {
        return nextTickTime - System.currentTimeMillis() <= 0;
    }

    private void calculateNextTickTime() {
        int waitTime = RandomUtils.getRandom(lowerBoundaryOfWait, upperBoundaryOfWait);
        long currentTimeMillis = System.currentTimeMillis();
        nextTickTime = currentTimeMillis + waitTime;
    }


}

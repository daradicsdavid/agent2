package application;

public class AgentConfiguration {

    private final int lowerPortBoundary = 20000;
    private final int upperPortBoundary = 20005;
    private final int numberOfFirstAgencyMembers;
    private final int numberOfSecondAgencyMembers;
    private final int lowerBoundaryOfWait;
    private final int upperBoundaryOfWait;

    public AgentConfiguration(int numberOfFirstAgencyMembers, int numberOfSecondAgencyMembers, int lowerBoundaryOfWait, int upperBoundaryOfWait) {
        this.numberOfFirstAgencyMembers = numberOfFirstAgencyMembers;
        this.numberOfSecondAgencyMembers = numberOfSecondAgencyMembers;
        this.lowerBoundaryOfWait = lowerBoundaryOfWait;
        this.upperBoundaryOfWait = upperBoundaryOfWait;
    }

    public int getNumberOfFirstAgencyMembers() {
        return numberOfFirstAgencyMembers;
    }

    public int getNumberOfSecondAgencyMembers() {
        return numberOfSecondAgencyMembers;
    }

    public int getLowerBoundaryOfWait() {
        return lowerBoundaryOfWait;
    }

    public int getUpperBoundaryOfWait() {
        return upperBoundaryOfWait;
    }

    public int getLowerPortBoundary() {
        return lowerPortBoundary;
    }

    public int getUpperPortBoundary() {
        return upperPortBoundary;
    }

    @Override
    public String toString() {
        return "AgentConfiguration{" +
                "numberOfFirstAgencyMembers=" + numberOfFirstAgencyMembers +
                ", numberOfSecondAgencyMembers=" + numberOfSecondAgencyMembers +
                ", lowerBoundaryOfWait=" + lowerBoundaryOfWait +
                ", upperBoundaryOfWait=" + upperBoundaryOfWait +
                '}';
    }
}

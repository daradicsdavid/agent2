package application;

public class AgentConfiguration {

    public static final int LOWER_PORT_BOUNDARY = 20000;
    public static final int UPPER_PORT_BOUNDARY = 20005;
    private final int numberOfFirstAgencyMembers;
    private final int numberOfSecondAgencyMembers;
    private final int lowerBoundaryOfWait;
    private final int upperBoundaryOfWait;

    AgentConfiguration(int numberOfFirstAgencyMembers, int numberOfSecondAgencyMembers, int lowerBoundaryOfWait, int upperBoundaryOfWait) {
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

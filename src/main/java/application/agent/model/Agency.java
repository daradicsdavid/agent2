package application.agent.model;

public enum Agency {
    FIRST(1), SECOND(2);

    private final int number;

    Agency(int number) {

        this.number = number;
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }

    public static Agency getAgencyByNumber(Integer number) {
        switch (number) {
            case 1:
                return FIRST;
            case 2:
                return SECOND;
            default:
                return null;
        }
    }
}

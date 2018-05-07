package application.agent.model;

import java.util.ArrayList;
import java.util.List;

public class KnownAgent {
    private final Agency agency;
    private Integer number;
    private List<Integer> wrongNumbers = new ArrayList<>();

    KnownAgent(Agency agency) {
        this.agency = agency;
    }

    public Agency getAgency() {
        return agency;
    }

    Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    List<Integer> getWrongNumbers() {
        return wrongNumbers;
    }

    public void addWrongNumber(Integer number) {
        wrongNumbers.add(number);
    }
}

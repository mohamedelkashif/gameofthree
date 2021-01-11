package com.gameofthree.game.entities.gametrun;

import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.entities.OutputNumber;

import java.util.Objects;

public class GameTurnInput {

    private final InputNumber inputNumber;
    private final OutputNumber lastOutputNumber;


    public GameTurnInput(InputNumber inputNumber, OutputNumber lastOutputNumber) {
        this.inputNumber = inputNumber;
        this.lastOutputNumber = lastOutputNumber;
    }

    public int getValue() {
        return inputNumber.getValue() + lastOutputNumber.getValue();
    }

    public boolean isBiggerOrEqualThan(int lowBoundary) {
        return getValue() >= lowBoundary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameTurnInput that = (GameTurnInput) o;
        return Objects.equals(inputNumber, that.inputNumber) &&
                Objects.equals(lastOutputNumber, that.lastOutputNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputNumber, lastOutputNumber);
    }

    @Override
    public String toString() {
        return "last round output was " +
                lastOutputNumber +
                " and your input was " +
                inputNumber;
    }



}

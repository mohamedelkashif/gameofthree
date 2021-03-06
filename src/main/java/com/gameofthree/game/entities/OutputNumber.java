package com.gameofthree.game.entities;

import com.gameofthree.utils.PropertiesConfigLoader;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class OutputNumber {

    private static final String FIXED_INPUT_NUMBER = PropertiesConfigLoader.getProperties().
            getProperty("random_start_input_number");

    private static final String MIN_POSSIBLE_INPUT_NUMBER = PropertiesConfigLoader.getProperties().
            getProperty("min_possible_start_input_number", "10");

    private static final String MAX_POSSIBLE_INPUT_NUMBER = PropertiesConfigLoader.getProperties().
            getProperty("max_possible_start_input_number", "100");


    private final int value;

    public OutputNumber(final int value) {
        this.value = value;
    }

    public static OutputNumber getStartNumber() {
        int minPossibleInputNumber = Integer.parseInt(MIN_POSSIBLE_INPUT_NUMBER);
        int maxPossibleInputNumber = Integer.parseInt(MAX_POSSIBLE_INPUT_NUMBER);

        int inputNumberValue = Optional.ofNullable(FIXED_INPUT_NUMBER)
                .map(Integer::parseInt)
                .orElse(getRandomIntBetween(minPossibleInputNumber, maxPossibleInputNumber));
        return new OutputNumber(inputNumberValue);
    }

    private static int getRandomIntBetween(int minPossibleInputNumber, int maxPossibleInputNumber) {
        return new Random().nextInt(maxPossibleInputNumber - minPossibleInputNumber) + minPossibleInputNumber;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputNumber that = (OutputNumber) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

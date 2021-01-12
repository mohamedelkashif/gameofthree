package com.gameofthree.game.service.computer;

import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.entities.OutputNumber;
import com.gameofthree.utils.PropertiesConfigLoader;

import java.util.HashMap;
import java.util.Map;

public class ComputerGameTurnService implements IComputerGameTurn {

    private static final int DIVIDER = Integer.parseInt(PropertiesConfigLoader.getProperties()
            .getProperty("divider"));


    private static final Map<Integer, Integer> ADDITION_VALUES;

    static {
        ADDITION_VALUES = new HashMap<>();
        ADDITION_VALUES.put(0, 0);
        ADDITION_VALUES.put(1, -1);
        ADDITION_VALUES.put(2, 1);
    }


    @Override
    public InputNumber calculateNextInputNumberFor(final OutputNumber outputNumber) {
        return getAdditionForClosestDivisibleValue(outputNumber);

    }

    private InputNumber getAdditionForClosestDivisibleValue(final OutputNumber outputNumber) {
        int remainder = outputNumber.getValue() % DIVIDER;
        int addition = ADDITION_VALUES.get(remainder);
        return new InputNumber(addition);
    }
}

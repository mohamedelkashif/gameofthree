package com.gameofthree.game.validators;

import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.exceptions.ValidationException;
import com.gameofthree.game.service.Game;
import com.gameofthree.utils.PropertiesConfigLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class InputNumberWithinRangeValidator implements Validator<Game> {

    private static final List<InputNumber> VALID_INPUT_NUMBERS = new CopyOnWriteArrayList<>();

    static {
        String[] additions = PropertiesConfigLoader.getProperties()
                .getProperty("input_numbers")
                .split("[/s,.]");
        Arrays.stream(additions).forEach(addition -> {
            int additionInt = Integer.parseInt(addition);
            VALID_INPUT_NUMBERS.add(new InputNumber(additionInt));
        });
    }

    private static final Function<InputNumber, String> INVALID_INPUT_FOR_GAME_MSG =
            number -> "can not play game because "+number+" is not within "+VALID_INPUT_NUMBERS;

    private List<String> messages = new ArrayList<>();
    private final InputNumber inputNumber;

    public InputNumberWithinRangeValidator(InputNumber inputNumber) {
        this.inputNumber = inputNumber;
    }

    @Override
    public boolean validate(Game obj) {
        return isValidInputNumber(inputNumber) ||
                setInvalidState(INVALID_INPUT_FOR_GAME_MSG.apply(inputNumber));
    }

    @Override
    public void validateOrThrow(Game game) throws ValidationException {
        if (!isValidInputNumber(inputNumber)) {
            throw new ValidationException(INVALID_INPUT_FOR_GAME_MSG.apply(inputNumber));
        }
    }

    @Override
    public List<String> getValidationMessages() {
        return messages;
    }

    private boolean isValidInputNumber(InputNumber inputNumber) {
        return VALID_INPUT_NUMBERS.contains(inputNumber);
    }

    private boolean setInvalidState(String message) {
        messages.add(message);
        return false;
    }
}

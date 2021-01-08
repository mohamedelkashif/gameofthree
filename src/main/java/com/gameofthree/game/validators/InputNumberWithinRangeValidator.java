package com.gameofthree.game.validators;

import com.gameofthree.game.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class InputNumberWithinRangeValidator implements Validator<Game> {
    private static final List<InputNumber> VALID_INPUT_NUMBERS = new CopyOnWriteArrayList<>();


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
    public void validateOrThrow(Game obj) throws ValidationException {
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

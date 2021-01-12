package com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.validator;

import com.gameofthree.game.entities.gametrun.GameTurnInput;
import com.gameofthree.game.exceptions.GameRoundException;
import com.gameofthree.game.exceptions.ValidationException;
import com.gameofthree.game.validators.Validator;
import com.gameofthree.utils.PropertiesConfigLoader;

import java.util.ArrayList;
import java.util.List;

public class DivideByThreeValidator implements Validator<GameTurnInput> {

    private static final String INVALID_INPUT_MSG = "could not play this round because of invalid input ";
    private static final int LOW_BOUNDARY = 2;
    private static final int DIVIDER = Integer.parseInt(PropertiesConfigLoader.getProperties()
            .getProperty("divider"));

    private List<String> messages = new ArrayList<>();

    @Override
    public boolean validate(GameTurnInput gameTurnInput) {
        return isValid(gameTurnInput) ||
                setInvalidState(INVALID_INPUT_MSG + gameTurnInput);
    }

    @Override
    public void validateOrThrow(GameTurnInput gameTurnInput) throws ValidationException {
        if (!isValid(gameTurnInput)) {
            throw new GameRoundException(INVALID_INPUT_MSG + gameTurnInput);
        }
    }

    @Override
    public List<String> getValidationMessages() {
        return messages;
    }

    private boolean isValid(GameTurnInput gameTurnInput) {
        return isBiggerThanLowBoundary(gameTurnInput) &&
                isDividableBy(gameTurnInput);
    }

    private boolean isBiggerThanLowBoundary(GameTurnInput gameTurnInput) {
        return gameTurnInput.isBiggerOrEqualThan(LOW_BOUNDARY);
    }

    private boolean isDividableBy(GameTurnInput gameTurnInput) {
        return gameTurnInput.getValue() % DIVIDER == 0;
    }

    private boolean setInvalidState(String message) {
        messages.add(message);
        return false;
    }
}

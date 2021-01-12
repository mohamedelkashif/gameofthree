package com.gameofthree.game.validators;

import com.gameofthree.game.exceptions.ValidationException;
import com.gameofthree.game.service.Game;

import java.util.ArrayList;
import java.util.List;

public class NewGameValidator implements Validator<Game> {

    static final String INVALID_PLAYER_AGGREGATE_MSG = "Game initialization failed due to invalid players.";
    private List<String> messages = new ArrayList<>();

    @Override
    public boolean validate(Game game) {
        return isValidPlayerAggregate(game) ||
                setInvalidState();
    }

    @Override
    public void validateOrThrow(Game game) throws ValidationException {
        if (!isValidPlayerAggregate(game)) {
            throw new ValidationException(INVALID_PLAYER_AGGREGATE_MSG);
        }
    }


    private boolean isValidPlayerAggregate(Game game) {
        return game.getPlayerAggregate().isValid();
    }

    private boolean setInvalidState() {
        messages.add(NewGameValidator.INVALID_PLAYER_AGGREGATE_MSG);
        return false;
    }
}

package com.gameofthree.game.validators;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class UniquePlayerValidator implements Validator<Game>  {

    private IPlayer newPlayer;
    private List<String> messages = new ArrayList<>();
    private static final String NOT_UNIQUE_MSG = "can not add another player ";



    public UniquePlayerValidator(IPlayer newPlayer) {
        this.newPlayer = newPlayer;
    }

    @Override
    public boolean validate(Game obj) {
        return !alreadyPresentPlayer(game.getPlayerAggregate(), newPlayer)
                || setInvalidState(NOT_UNIQUE_MSG);
    }

    @Override
    public void validateOrThrow(Game obj) throws ValidationException {
        if (alreadyPresentPlayer(game.getPlayerAggregate(), newPlayer)) {
            throw new ValidationException(NOT_UNIQUE_MSG);
        }

    }

    @Override
    public List<String> getValidationMessages() {
        return messages;
    }

    private boolean setInvalidState(String message) {
        messages.add(message);
        return false;
    }
}

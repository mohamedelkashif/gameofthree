package com.gameofthree.game.validators;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.exceptions.ValidationException;
import com.gameofthree.game.service.Game;

import java.util.ArrayList;
import java.util.List;

public class UniquePlayerValidator implements Validator<Game> {

    private static final String NOT_UNIQUE_MSG = "can not add another player";
    private IPlayer newPlayer;
    private List<String> messages = new ArrayList<>();


    public UniquePlayerValidator(IPlayer newPlayer) {
        this.newPlayer = newPlayer;
    }

    @Override
    public boolean validate(Game game) {
        return !alreadyPresentPlayer(game.getPlayerAggregate(), newPlayer)
                || setInvalidState();
    }

    @Override
    public void validateOrThrow(Game game) {
        if (alreadyPresentPlayer(game.getPlayerAggregate(), newPlayer)) {
            throw new ValidationException(NOT_UNIQUE_MSG);
        }

    }


    private boolean setInvalidState() {
        messages.add(UniquePlayerValidator.NOT_UNIQUE_MSG);
        return false;
    }

    private boolean alreadyPresentPlayer(PlayerAggregate playerAggregate, IPlayer newPlayer) {
        return playerAggregate.hasPlayer(newPlayer);
    }
}

package com.gameofthree.game.validators;

import com.gameofthree.game.exceptions.ValidationException;
import com.gameofthree.game.service.Game;

import java.util.ArrayList;
import java.util.List;

public class IsGameOpenForMorePlayersValidator implements Validator<Game> {

    static final String IS_OPEN_PLAYER_AGGREGATE_MSG = "limit reached, can not add more ";
    private List<String> messages = new ArrayList<>();

    @Override
    public boolean validate(Game game) {
        return isGameOpenForMorePlayers(game) || setInvalidState(IS_OPEN_PLAYER_AGGREGATE_MSG +game.getPlayerAggregate());
    }

    @Override
    public void validateOrThrow(Game game) throws ValidationException {
        if (!isGameOpenForMorePlayers(game)) {
            throw new ValidationException(IS_OPEN_PLAYER_AGGREGATE_MSG + game.getPlayerAggregate());
        }
    }

    @Override
    public List<String> getValidationMessages() {
        return messages;
    }

    private boolean isGameOpenForMorePlayers(Game game) {
        return game.getPlayerAggregate().acceptsMorePlayers();
    }

    private boolean setInvalidState(String message) {
        messages.add(message);
        return false;
    }
}

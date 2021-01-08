package com.gameofthree.game.validators;

import com.gameofthree.game.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CanPlayGameValidator implements Validator<Game> {

    static final String INVALID_GAME_ROUND_STATE_MSG = "can not play game when ";
    static final String INVALID_PLAYER_AGGREGATE_MSG = "can not play game when ";

    private List<String> messages = new ArrayList<>();

    @Override
    public boolean validate(Game obj) {
        return Stream.of(
                isValidGameRoundResult(game) || setInvalidState(INVALID_GAME_ROUND_STATE_MSG+game.getGameRoundResult()),
                isValidPlayerAggregate(game) || setInvalidState(INVALID_PLAYER_AGGREGATE_MSG+game.getPlayerAggregate())
        ).allMatch(Boolean::booleanValue);    }

    @Override
    public void validateOrThrow(Game obj) throws ValidationException {
        if (!isValidGameRoundResult(game)) {
            throw new ValidationException(INVALID_GAME_ROUND_STATE_MSG+game.getGameRoundResult());
        }

        if (!isValidPlayerAggregate(game)) {
            throw new GameException(INVALID_PLAYER_AGGREGATE_MSG + game.getPlayerAggregate());
        }
    }

    @Override
    public List<String> getValidationMessages() {
        return messages;
    }

    private boolean isValidGameRoundResult(Game game) {
        return game.getGameRoundResult().canPlayAgain();
    }

    private boolean isValidPlayerAggregate(Game game) {
        return game.getPlayerAggregate().isValid();
    }

    private boolean setInvalidState(String message) {
        messages.add(message);
        return false;
    }
}
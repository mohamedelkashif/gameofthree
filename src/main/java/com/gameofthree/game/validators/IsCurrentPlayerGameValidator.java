package com.gameofthree.game.validators;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.exceptions.NotCurrentPlayerException;
import com.gameofthree.game.exceptions.ValidationException;
import com.gameofthree.game.service.Game;

import java.util.ArrayList;
import java.util.List;

public class IsCurrentPlayerGameValidator implements Validator<Game> {

    static final String INVALID_INPUT_FOR_GAME_MSG = "It is not your turn to play.";

    private List<String> messages = new ArrayList<>();

    private IPlayer expectedPlayer;

    public IsCurrentPlayerGameValidator(IPlayer expectedPlayer) {
        this.expectedPlayer = expectedPlayer;
    }


    @Override
    public boolean validate(Game game) {
        return isNextTurnPlayer(game, expectedPlayer) ||
                setInvalidState(INVALID_INPUT_FOR_GAME_MSG);
    }

    @Override
    public void validateOrThrow(Game game) throws ValidationException {
        if (!isNextTurnPlayer(game, expectedPlayer)) {
            throw new NotCurrentPlayerException(INVALID_INPUT_FOR_GAME_MSG);
        }
    }


    private boolean isNextTurnPlayer(Game game, IPlayer expectedCurrentPlayer) {
        IPlayer gameCurrentPlayer = game.getPlayerAggregate().getRootPlayer();
        return gameCurrentPlayer.isSame(expectedCurrentPlayer);
    }

    private boolean setInvalidState(String message) {
        messages.add(message);
        return false;
    }
}

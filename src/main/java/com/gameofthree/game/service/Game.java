package com.gameofthree.game.service;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.entities.gametrun.GameTurnInput;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.service.gameturn.IGameTurnService;
import com.gameofthree.game.validators.CanValidate;
import com.gameofthree.game.validators.Validator;

import java.util.Objects;

public class Game implements CanValidate<Game> {

    private final IGameTurnService gameTurnService;
    private final PlayerAggregate playerAggregate;
    private final GameTurnResult gameTurnResult;

    public Game(final IGameTurnService gameTurnService) {
        this.gameTurnService = gameTurnService;
        this.playerAggregate = PlayerAggregate.NULL;
        this.gameTurnResult = GameTurnResult.NULL;
    }

    public Game(final IGameTurnService gameTurnService, final PlayerAggregate playerAggregate, final GameTurnResult gameRoundResult) {
        this.gameTurnService = gameTurnService;
        this.playerAggregate = playerAggregate;
        this.gameTurnResult = gameRoundResult;
    }

    public Game startGame() {
        return new Game(gameTurnService, playerAggregate, GameTurnResult.getInitial());
    }

    public Game stopGame() {
        return new Game(gameTurnService, playerAggregate, GameTurnResult.NULL);
    }

    public Game addPlayer(final IPlayer player) {
        return new Game(gameTurnService, playerAggregate.addPlayer(player), gameTurnResult);
    }

    public Game removePlayer(IPlayer player) {
        return new Game(gameTurnService, playerAggregate.removePlayer(player), gameTurnResult);
    }

    public Game play(final InputNumber inputNumber) {
        GameTurnInput gameRoundInput = new GameTurnInput(inputNumber, gameTurnResult.getOutputNumber());
        return new Game(gameTurnService, playerAggregate.next(), gameTurnService.play(gameRoundInput));
    }

    public GameTurnResult getGameTurnResult() {
        return gameTurnResult;
    }


    public PlayerAggregate getPlayerAggregate() {
        return playerAggregate;
    }


    @Override
    public void validateOrThrow(Validator<Game> validator) {
        validator.validateOrThrow(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(gameTurnService, game.gameTurnService) &&
                Objects.equals(playerAggregate, game.playerAggregate) &&
                Objects.equals(gameTurnResult, game.gameTurnResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameTurnService, playerAggregate, gameTurnResult);
    }

    @Override
    public String toString() {
        return playerAggregate +
                " and " +
                gameTurnResult;
    }
}

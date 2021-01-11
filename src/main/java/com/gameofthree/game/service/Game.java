package com.gameofthree.game.service;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.validators.CanValidate;
import com.gameofthree.game.validators.Validator;

import java.util.Objects;

public class Game implements CanValidate<Game> {

    private final IGameRoundService gameRoundService;
    private final PlayerAggregate playerAggregate;
    private final GameRoundResult gameRoundResult;

    public Game(final IGameRoundService gameRoundService) {
        this.gameRoundService = gameRoundService;
        this.playerAggregate = PlayerAggregate.NULL;
        this.gameRoundResult = GameRoundResult.NULL;
    }

    public Game(final IGameRoundService gameRoundService, final PlayerAggregate playerAggregate, final GameRoundResult gameRoundResult) {
        this.gameRoundService = gameRoundService;
        this.playerAggregate = playerAggregate;
        this.gameRoundResult = gameRoundResult;
    }

    public Game startGame() {
        return new Game(gameRoundService, playerAggregate, GameRoundResult.getInitial());
    }

    public Game stopGame() {
        return new Game(gameRoundService, playerAggregate, GameRoundResult.NULL);
    }

    public Game addPlayer(final IPlayer player) {
        return new Game(gameRoundService, playerAggregate.addPlayer(player), gameRoundResult);
    }

    public Game removePlayer(IPlayer player) {
        return new Game(gameRoundService, playerAggregate.removePlayer(player), gameRoundResult);
    }

    public Game play(final InputNumber inputNumber) {
        GameRoundInput gameRoundInput = new GameRoundInput(inputNumber, gameRoundResult.getOutputNumber());
        return new Game(gameRoundService, playerAggregate.next(), gameRoundService.play(gameRoundInput));
    }

    public GameRoundResult getGameRoundResult() {
        return gameRoundResult;
    }


    public PlayerAggregate getPlayerAggregate() {
        return playerAggregate;
    }


    @Override
    public boolean validate(Validator<Game> validator) {
        return validator.validate(this);
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
        return Objects.equals(gameRoundService, game.gameRoundService) &&
                Objects.equals(playerAggregate, game.playerAggregate) &&
                Objects.equals(gameRoundResult, game.gameRoundResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameRoundService, playerAggregate, gameRoundResult);
    }

    @Override
    public String toString() {
        return playerAggregate +
                " and " +
                gameRoundResult;
    }
}

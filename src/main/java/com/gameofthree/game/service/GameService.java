package com.gameofthree.game.service;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.validators.NewGameValidator;
import com.gameofthree.game.validators.UniquePlayerValidator;
import com.gameofthree.game.validators.isGameOpenForMorePlayersValidator;

import java.util.concurrent.atomic.AtomicReference;

public class GameService implements IGameService {

    private final AtomicReference<Game> game;


    @Override
    public void addPlayer(IPlayer player) {
        new UniquePlayerValidator(player).validateOrThrow(game.get());
        new isGameOpenForMorePlayersValidator().validateOrThrow(game.get());
        game.set(game.get().addPlayer(player));
    }

    @Override
    public void removePlayer(IPlayer player) {
        game.set(game.get().removePlayer(player));
    }

    @Override
    public void startGame() {
        game.get().validateOrThrow(new NewGameValidator());
        game.set(game.get().startGame());
    }

    @Override
    public void stopGame() {
        game.set(game.get().stopGame());
    }

    @Override
    public void play(InputNumber inputNumber, IPlayer playerInTurn) {
        game.get().validateOrThrow(new CanPlayGameValidator());
        game.get().validateOrThrow(new IsCurrentPlayerGameValidator(playerInTurn));
        game.get().validateOrThrow(new InputNumberWithinRangeValidator(inputNumber));

        Game newGame = game.get().play(inputNumber);
        game.set(newGame);
    }

    @Override
    public Game getGame() {
        return game.get();
    }
}

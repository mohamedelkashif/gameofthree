package com.gameofthree.game.service;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.InputNumber;

public interface IGameService {

    void addPlayer(final IPlayer player);

    void removePlayer(IPlayer player);

    void startGame();

    void stopGame();

    void play(final InputNumber inputNumber, final IPlayer playerInTurn);

    Game getGame();
}

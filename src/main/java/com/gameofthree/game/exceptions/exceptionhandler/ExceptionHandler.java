package com.gameofthree.game.exceptions.exceptionhandler;

import com.gameofthree.game.entities.IPlayer;

public interface ExceptionHandler<E extends RuntimeException> {
    void handle(E ex, IPlayer currentPlayer);

}

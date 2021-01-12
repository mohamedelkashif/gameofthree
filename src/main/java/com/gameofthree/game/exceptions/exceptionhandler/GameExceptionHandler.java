package com.gameofthree.game.exceptions.exceptionhandler;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.exceptions.GameException;
import com.gameofthree.server.sockets.ISocketIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameExceptionHandler implements ExceptionHandler<GameException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameExceptionHandler.class);

    private ISocketIOHandler socketIOHandler;

    public GameExceptionHandler(ISocketIOHandler socketIOHandler) {
        this.socketIOHandler = socketIOHandler;
    }


    @Override
    public void handle(GameException ex, IPlayer currentPlayer) {
        LOGGER.debug("GameException for player {}. Error message: {}.", currentPlayer, ex.getMessage());
        String errMessage = buildErrorMessage(ex);
        socketIOHandler.send(errMessage);
    }

    private String buildErrorMessage(GameException ex) {
        return "ERROR: " + ex.getMessage();
    }
}

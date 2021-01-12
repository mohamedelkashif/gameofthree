package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.Human;
import com.gameofthree.game.exceptions.GameException;
import com.gameofthree.game.exceptions.exceptionhandler.GameExceptionHandler;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class AddHumanCommand extends GameCommand<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;


    public AddHumanCommand(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }

    @Override
    public void execute(String data) {
        Human authorizedPlayer = new Human(Thread.currentThread().getName(), data);

        try {
            gameService.addPlayer(authorizedPlayer);
        } catch (GameException ex) {
            new GameExceptionHandler(socketIOHandler).handle(ex, authorizedPlayer);
            return;
        }

        socketIOHandler.broadcast("Added player " + authorizedPlayer.getName() + " to game.");

        this.doNext(data);
    }
}

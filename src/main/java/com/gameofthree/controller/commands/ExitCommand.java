package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.Human;
import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class ExitCommand extends GameCommand<String>  {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;

    public ExitCommand(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }

    @Override
    public void execute(String data) {
        IPlayer authorizedPlayer = new Human(Thread.currentThread().getName(), "");  //inject authorized user
        gameService.removePlayer(authorizedPlayer);
        gameService.stopGame();

        socketIOHandler.send("Goodbye.");
        doNext(data);
    }
}

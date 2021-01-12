package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.Computer;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class AddComputerCommand extends GameCommand<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;


    public AddComputerCommand(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }

    @Override
    public void execute(String data) {
        Computer newPlayer = Computer.generate();

        gameService.addPlayer(newPlayer);
        socketIOHandler.broadcast("Added AI player " + newPlayer.getName() + " to game.");

        doNext(data);
    }
}

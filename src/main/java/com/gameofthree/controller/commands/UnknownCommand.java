package com.gameofthree.controller.commands;

import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class UnknownCommand extends GameCommand<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;


    public UnknownCommand(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }
    @Override
    public void execute(String data) {
        socketIOHandler.send("unknown command. Available commands are: ADD_PLAYER:player_name, ADD_MACHINE, START, PLAY:number, STATE, EXIT.");
        doNext(data);

    }
}

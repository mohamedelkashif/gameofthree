package com.gameofthree.controller.commands;

import com.gameofthree.server.sockets.ISocketIOHandler;

import static com.gameofthree.enums.CommandType.ADD_MACHINE;
import static com.gameofthree.enums.CommandType.ADD_PLAYER;
import static com.gameofthree.enums.CommandType.EXIT;
import static com.gameofthree.enums.CommandType.PLAY;
import static com.gameofthree.enums.CommandType.START;
import static com.gameofthree.enums.CommandType.STATE;

public class UnknownCommand extends GameCommand<String> {

    private ISocketIOHandler socketIOHandler;


    public UnknownCommand(ISocketIOHandler socketIOHandler) {
        this.socketIOHandler = socketIOHandler;
    }

    @Override
    public void execute(String data) {
        socketIOHandler.send("unknown command. Available commands are: " +
                ADD_PLAYER + ":player_name" +
                ADD_MACHINE +
                START +
                PLAY + ":number" +
                STATE +
                EXIT);
        doNext(data);

    }
}

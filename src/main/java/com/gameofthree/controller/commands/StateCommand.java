package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.service.Game;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class StateCommand extends GameCommand<String>{

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;


    public StateCommand(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }

    @Override
    public void execute(String data) {
        Game currentGame = gameService.getGame();

        PlayerAggregate players = currentGame.getPlayerAggregate();
        GameTurnResult currentRoundResult = currentGame.getGameTurnResult();

        String finalMessage = buildFinalMessage(players, currentRoundResult);
        socketIOHandler.send(finalMessage);

        doNext(data);
    }

    private String buildFinalMessage(PlayerAggregate players, GameTurnResult currentRoundResult) {
        return "Currently playing "+
                        players + " Last " +
                        currentRoundResult;
    }
}

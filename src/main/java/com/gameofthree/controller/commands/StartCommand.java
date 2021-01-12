package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.Human;
import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.exceptions.GameException;
import com.gameofthree.game.exceptions.exceptionhandler.GameExceptionHandler;
import com.gameofthree.game.service.Game;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class Start extends GameCommand<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;

    public Start(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }

    @Override
    public void execute(String data) {
        Human authorizedCurrentPlayer = new Human(Thread.currentThread().getName(), "");
        try {
            gameService.startGame();
        } catch (GameException e) {
            new GameExceptionHandler(socketIOHandler).handle(e, authorizedCurrentPlayer);
            return;
        }

        Game gameAfterStart = gameService.getGame();
        String finalMessage = buildFinalMessage(gameAfterStart);

        socketIOHandler.broadcast(finalMessage);

        GameTurnResult gameRoundResultAfterStart = gameAfterStart.getGameTurnResult();
        doNext(String.valueOf(gameRoundResultAfterStart.getOutputNumber().getValue()));
    }

    private String buildFinalMessage(Game gameAfterStart) {
        PlayerAggregate playerAfterStart = gameAfterStart.getPlayerAggregate();
        GameTurnResult gameRoundEndResult = gameAfterStart.getGameTurnResult();

        return "Game started. The starting number is " +
                gameRoundEndResult.getOutputNumber() +
                "." +
                " Next to play is " + playerAfterStart.getRootPlayer() +
                ".";
    }
}

package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.Human;
import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.exceptions.GameException;
import com.gameofthree.game.exceptions.exceptionhandler.GameExceptionHandler;
import com.gameofthree.game.service.Game;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class PlayCommand extends GameCommand<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;


    public PlayCommand(IGameService gameService, ISocketIOHandler socketIOHandler) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
    }


    @Override
    public void execute(String data) {
        IPlayer authorizedPlayer = new Human(Thread.currentThread().getName(), "");
        InputNumber parsedRawInputNumber = parseRawInputNumber(data);

        Game gameBeforePlay = gameService.getGame();
        PlayerAggregate playersBeforePlay = gameBeforePlay.getPlayerAggregate();

        try {
            gameService.play(parsedRawInputNumber, authorizedPlayer);
        } catch (GameException ex) {
            new GameExceptionHandler(socketIOHandler).handle(ex, authorizedPlayer);
            return;
        }

        Game gameAfterPlay = gameService.getGame();
        String message = buildResponseMsg(playersBeforePlay.getRootPlayer(), gameAfterPlay, data);

        socketIOHandler.broadcast(message);

        GameTurnResult gameRoundResultAfterPlay = gameAfterPlay.getGameTurnResult();
        doNext(String.valueOf(gameRoundResultAfterPlay.getOutputNumber().getValue()));

    }

    private String buildResponseMsg(IPlayer playingCurrentPlayer, Game gameAfterPlay, String inputNumber) {
        GameTurnResult gameAfterPlayGameTurnResult = gameAfterPlay.getGameTurnResult();

        return playingCurrentPlayer +
                " played number " +
                inputNumber +
                ". The result is " +
                gameAfterPlayGameTurnResult;
    }

    private InputNumber parseRawInputNumber(String rawInputNumber) {
        try {
            return new InputNumber(Integer.parseInt(rawInputNumber));
        } catch (NumberFormatException ex) {
            socketIOHandler.send("ERROR: " + ex.getMessage());
        }
        return null;
    }
}

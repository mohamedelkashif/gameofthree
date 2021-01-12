package com.gameofthree.controller.commands;

import com.gameofthree.game.entities.IPlayer;
import com.gameofthree.game.entities.InputNumber;
import com.gameofthree.game.entities.OutputNumber;
import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.service.Game;
import com.gameofthree.game.service.IGameService;
import com.gameofthree.game.service.computer.IComputerGameTurn;
import com.gameofthree.server.sockets.ISocketIOHandler;

public class PlayComputerCommand extends GameCommand<String> {

    private IGameService gameService;
    private ISocketIOHandler socketIOHandler;
    private IComputerGameTurn computerGameTurn;


    public PlayComputerCommand(IGameService gameService, ISocketIOHandler socketIOHandler, IComputerGameTurn computerGameTurn) {
        this.gameService = gameService;
        this.socketIOHandler = socketIOHandler;
        this.computerGameTurn = computerGameTurn;
    }

    @Override
    public void execute(String data) {
        playMachineRecursive();

        Game gameAfterPlay = gameService.getGame();
        GameTurnResult gameRoundResultAfterPlay = gameAfterPlay.getGameTurnResult();
        doNext(String.valueOf(gameRoundResultAfterPlay.getOutputNumber().getValue()));
    }

    private void playMachineRecursive() {
        Game gameBeforePlay = gameService.getGame();
        PlayerAggregate playersBeforePlay = gameBeforePlay.getPlayerAggregate();
        IPlayer nextPlayer = playersBeforePlay.getRootPlayer();
        OutputNumber lastOutputNumber = gameBeforePlay.getGameTurnResult().getOutputNumber();

        if (gameBeforePlay.getGameTurnResult().isWinner()) return;

        if (nextPlayer.isComputer()) {
            InputNumber calculatedNumberByAi = computerGameTurn.calculateNextInputNumberFor(lastOutputNumber);

            gameService.play(calculatedNumberByAi, nextPlayer);

            Game gameAfterPlay = gameService.getGame();
            String message2 = buildResponseMsg(nextPlayer, gameAfterPlay, calculatedNumberByAi.toString());

            socketIOHandler.broadcast(message2);

            playMachineRecursive();
        }
    }

    private String buildResponseMsg(IPlayer playingCurrentPlayer, Game gameAfterPlay, String inputNumber) {
        GameTurnResult gameAfterPlayGameTurnResult = gameAfterPlay.getGameTurnResult();

        return "\n"+playingCurrentPlayer +
                " played number " +
                inputNumber +
                ". The result is " +
                gameAfterPlayGameTurnResult;
    }
}

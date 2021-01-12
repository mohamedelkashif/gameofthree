package com.gameofthree.game.service;

import com.gameofthree.game.entities.PlayerAggregate;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.service.gameturn.GameTurnService;
import com.gameofthree.game.service.gameturn.IGameTurnService;
import com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.IGameTurnLogic;
import com.gameofthree.game.service.gameturn.gamerules.gamewinlogic.IGameWinLogic;

public class GameFactory {

    private IGameTurnLogic gameLogic;
    private IGameWinLogic winLogic;

    public GameFactory(IGameTurnLogic gameLogic, IGameWinLogic winLogic) {
        this.gameLogic = gameLogic;
        this.winLogic = winLogic;
    }

    public Game buildNewGame() {
        IGameTurnService gameRoundService = new GameTurnService(gameLogic, winLogic);

        return new Game(gameRoundService, PlayerAggregate.NULL, GameTurnResult.NULL);
    }

}

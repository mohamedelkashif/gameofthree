package com.gameofthree.game.service.gameturn;

import com.gameofthree.game.entities.OutputNumber;
import com.gameofthree.game.entities.gametrun.GameTurnInput;
import com.gameofthree.game.entities.gametrun.GameTurnResult;
import com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.IGameTurnLogic;
import com.gameofthree.game.service.gameturn.gamerules.gamewinlogic.IGameWinLogic;

public class GameTurnService implements IGameTurnService {

    private final IGameTurnLogic gameTurnLogic;
    private final IGameWinLogic winLogic;

    public GameTurnService(final IGameTurnLogic gameTurnLogic, final IGameWinLogic winLogic) {
        this.gameTurnLogic = gameTurnLogic;
        this.winLogic = winLogic;
    }

    @Override
    public GameTurnResult play(GameTurnInput gameTurnInput) {
        OutputNumber outputNumber = gameTurnLogic.apply(gameTurnInput);
        boolean winner = winLogic.apply(outputNumber);

        return new GameTurnResult(outputNumber, winner);    }
}

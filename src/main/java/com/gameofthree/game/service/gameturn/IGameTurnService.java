package com.gameofthree.game.service.gameturn;

import com.gameofthree.game.entities.gametrun.GameTurnInput;
import com.gameofthree.game.entities.gametrun.GameTurnResult;

public interface IGameTurnService {

    GameTurnResult play(final GameTurnInput gameTurnInput);

}

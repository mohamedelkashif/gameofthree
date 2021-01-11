package com.gameofthree.game.service.gameturn.gamerules.gameplaylogic;

import com.gameofthree.game.entities.OutputNumber;
import com.gameofthree.game.entities.gametrun.GameTurnInput;

import java.util.function.Function;

public interface IGameTurnLogic extends Function<GameTurnInput, OutputNumber>, Validatable<GameTurnInput> {
}

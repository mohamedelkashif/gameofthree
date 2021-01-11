package com.gameofthree.game.service.gameturn.gamerules.gamewinlogic;

import com.gameofthree.game.entities.OutputNumber;

import java.util.function.Function;

@FunctionalInterface
public interface IGameWinLogic extends Function<OutputNumber, Boolean> {
}

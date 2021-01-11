package com.gameofthree.game.service.gameturn.gamerules.gamewinlogic;

import com.gameofthree.game.entities.OutputNumber;

public class GameWinLogic implements IGameWinLogic {

    private static final int WINNING_VALUE = 1;


    @Override
    public Boolean apply(OutputNumber outputNumber) {
        return outputNumber.getValue() == WINNING_VALUE;
    }
}

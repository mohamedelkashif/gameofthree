package com.gameofthree.game.entities.gametrun;

import com.gameofthree.game.entities.OutputNumber;

import java.util.Objects;

public class GameTurnResult {

    public static final GameTurnResult NULL = new GameTurnResult(null, false);

    private final OutputNumber outputNumber;
    private final boolean winner;

    public GameTurnResult(OutputNumber outputNumber, boolean winner) {
        this.outputNumber = outputNumber;
        this.winner = winner;
    }

    public static GameTurnResult getInitial() {
        return new GameTurnResult(OutputNumber.getStartNumber(), false);
    }

    public OutputNumber getOutputNumber() {
        return outputNumber;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean canPlayAgain() {
        return !isWinner();
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputNumber, winner);
    }

    @Override
    public String toString() {
        return "Round result:" +
                "outputNumber " +
                outputNumber +
                ", winner " +
                winner +
                ".";
    }



}

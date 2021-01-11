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
        final StringBuffer sb = new StringBuffer("Round result: ");
        sb.append("outputNumber ").append(outputNumber);
        sb.append(", winner ").append(winner);
        sb.append('.');
        return sb.toString();
    }



}

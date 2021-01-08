package com.gameofthree.game.entities;

public interface IPlayer {

    String getId();
    String getName();
    boolean isSame(IPlayer player);
    boolean isComputer();
}

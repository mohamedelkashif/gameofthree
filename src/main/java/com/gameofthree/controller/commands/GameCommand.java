package com.gameofthree.controller.commands;

import java.util.Optional;

public abstract class GameCommand<T> {

    protected Optional<GameCommand<T>> successor = Optional.empty();

    public abstract void execute(T data);

    public void setSuccessor(GameCommand<T> successor) {
        this.successor = Optional.of(successor);
    }

    protected void doNext(T data) {
        successor.ifPresent(s -> s.execute(data));
    }

}

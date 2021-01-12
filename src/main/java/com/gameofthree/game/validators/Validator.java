package com.gameofthree.game.validators;

public interface Validator<T> {
    boolean validate(T obj);

    void validateOrThrow(T obj);
}

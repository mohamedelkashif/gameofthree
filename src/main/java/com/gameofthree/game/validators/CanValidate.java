package com.gameofthree.game.validators;

public interface CanValidate<T> {

    boolean validate(Validator<T> validator);
    void validateOrThrow(Validator<T> validator);


}

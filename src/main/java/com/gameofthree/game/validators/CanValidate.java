package com.gameofthree.game.validators;

public interface CanValidate<T> {

    void validateOrThrow(Validator<T> validator);


}

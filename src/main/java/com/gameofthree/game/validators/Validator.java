package com.gameofthree.game.validators;

import com.gameofthree.game.exceptions.ValidationException;

import java.util.List;

public interface Validator<T> {
    boolean validate(T obj);
    void validateOrThrow(T obj) throws ValidationException;
    List<String> getValidationMessages();


}

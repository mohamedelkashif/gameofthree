package com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.validator;

import com.gameofthree.game.validators.Validator;

public interface Validatable<T> {
    void addValidator(Validator<T> validator);
}

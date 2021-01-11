package com.gameofthree.game.service.gameturn.gamerules.gameplaylogic;

import com.gameofthree.game.entities.OutputNumber;
import com.gameofthree.game.entities.gametrun.GameTurnInput;
import com.gameofthree.game.validators.Validator;
import com.gameofthree.utils.PropertiesConfigLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DivideByThreeLogic implements IGameTurnLogic  {

    private static final int DIVIDER = Integer.parseInt(PropertiesConfigLoader.getProperties()
            .getProperty("com.challenge.application.game.divider"));

    private final List<Validator<GameTurnInput>> validators;

    public DivideByThreeLogic() {
        this.validators = new ArrayList<>();
    }


    @Override
    public OutputNumber apply(GameTurnInput gameTurnInput) {
        validateOrThrow(gameTurnInput);
        return applyLogic(gameTurnInput);
    }

    @Override
    public <V> Function<V, OutputNumber> compose(Function<? super V, ? extends GameTurnInput> before) {
        return null;
    }

    @Override
    public <V> Function<GameTurnInput, V> andThen(Function<? super OutputNumber, ? extends V> after) {
        return null;
    }

    public void addValidator(Validator<GameTurnInput> validator) {
        validators.add(validator);
    }

    private void validateOrThrow(GameTurnInput gameTurnInput) {
        validators.forEach(validator -> validator.validateOrThrow(gameTurnInput));
    }

    private OutputNumber applyLogic(GameTurnInput gameTurnInput) {
        return new OutputNumber(gameTurnInput.getValue() / DIVIDER);
    }

}

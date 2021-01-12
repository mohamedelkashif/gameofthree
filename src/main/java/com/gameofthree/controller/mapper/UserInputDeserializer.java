package com.gameofthree.controller.mapper;

import com.gameofthree.game.dtos.UserInputDTO;

import java.util.function.Function;

public class UserInputDeserializer implements Function<String, UserInputDTO> {
    @Override
    public UserInputDTO apply(String input) {
        String[] rawUserInputArray = input.split(":", 2);
        String command = rawUserInputArray[0];
        String data = rawUserInputArray.length > 1 ? rawUserInputArray[1] : "";

        return new UserInputDTO(command, data);
    }
}

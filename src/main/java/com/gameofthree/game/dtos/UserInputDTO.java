package com.gameofthree.game.dtos;

import java.util.Optional;

public class UserInputDTO {

    private String command;
    private String data;

    public UserInputDTO(String command, String data) {
        this.command = command;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public String getData() {
        return data;
    }

    public boolean isValid() {
        return Optional.ofNullable(command)
                .filter(s -> !s.isEmpty())
                .isPresent();
    }
}

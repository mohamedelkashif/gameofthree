package com.gameofthree.enums;

import java.util.Arrays;

public enum CommandType {
    ADD_PLAYER,
    ADD_MACHINE,
    START,
    PLAY,
    STATE,
    EXIT,
    UNKNOWN;

    public static CommandType valueOfString(final String commandTypeStr) {
        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.toString().equals(commandTypeStr))
                .findFirst()
                .orElse(UNKNOWN);
    }
}

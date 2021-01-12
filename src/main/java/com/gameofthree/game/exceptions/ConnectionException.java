package com.gameofthree.game.exceptions;

import java.io.IOException;

public class ConnectionException extends RuntimeException {
    public ConnectionException(String message, IOException ex) {
        super(message, ex);
    }
}

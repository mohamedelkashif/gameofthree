package com.gameofthree;

import com.gameofthree.server.GameServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class GameOfThreeServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameOfThreeServer.class);

    private static final String PORT = "8080";
    public static void main(String [] args) throws IOException {

        LOGGER.info("The game has been started");

        // main logic here
        try (GameServer gameServer = new GameServer(Integer.parseInt(PORT))){
            ServerSocket serverSocket = gameServer.start();

        }

        LOGGER.info("The Game has been ended / closed");
    }
}

package com.gameofthree.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class GameServer implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);

    private int serverPort;
    private ServerSocket serverSocket;

    public GameServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public ServerSocket start(){
        try {
            serverSocket = new ServerSocket(serverPort);
            return serverSocket;
        }catch (IOException ioException){
            throw new RuntimeException("Exception when opening server socket");
        }
    }

    @Override
    public void close() throws IOException {
        try{
            serverSocket.close();
            LOGGER.warn("Server is closing");
            for(;;){
                if(serverSocket.isClosed())
                    return;
            }
        }catch (IOException ioException){
            throw new RuntimeException("Exception when closing server socket");
        }

    }
}

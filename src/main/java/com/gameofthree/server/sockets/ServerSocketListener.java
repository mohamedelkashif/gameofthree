package com.gameofthree.server.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class ServerSocketListener implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSocketListener.class);
    private ServerSocket serverSocket;
    private ThreadLocal<GameService> gameServiceThreadLocal;

    public ServerSocketListener(ServerSocket serverSocket, ThreadLocal<GameService> gameServiceThreadLocal) {
        this.serverSocket = serverSocket;
        this.gameServiceThreadLocal = gameServiceThreadLocal;
    }

    @Override
    public void run() {

        try (ServerStream serverStream = new ServerStream(serverSocket)){
            SocketIOHandler socketIOHandler = serverStream.start();
            socketIOHandler.setActiveSocketChannels();

            socketIOHandler.send("Connected");
            socketIOHandler.getInputStream()
                    .peek()
                    .filter(command -> command.equals("EXIT"))
                    .findAny();
            LOGGER.debug("Socket listener shutting down");
        }catch (Exception ex){
            LOGGER.error("Socket listener exited with this exception", ex);
        }
    }
}

package com.gameofthree.server.sockets;

import com.gameofthree.controller.GameCommandController;
import com.gameofthree.controller.mapper.UserInputDeserializer;
import com.gameofthree.game.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class ServerSocketListener implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSocketListener.class);

    private ServerSocket serverSocket;
    private ThreadLocal<GameService> gameServiceThreadLocal;
    private ThreadLocal<SocketChannelRegistry> socketChannelRegistry;


    public ServerSocketListener(ServerSocket serverSocket,
                                ThreadLocal<SocketChannelRegistry> socketChannelRegistry,
                                ThreadLocal<GameService> gameServiceThreadLocal
    ) {
        this.serverSocket = serverSocket;
        this.gameServiceThreadLocal = gameServiceThreadLocal;
        this.socketChannelRegistry = socketChannelRegistry;
    }

    @Override
    public void run() {

        try (ServerStream serverStream = new ServerStream(serverSocket)) {
            SocketIOHandler socketIOHandler = serverStream.start();
            socketChannelRegistry.get().register(Thread.currentThread().getName(), socketIOHandler);
            socketIOHandler.setActiveSocketChannels(socketChannelRegistry.get().getActiveSocketChannels());

            GameCommandController gameCommandController = new GameCommandController(gameServiceThreadLocal.get(), socketIOHandler, new UserInputDeserializer());

            socketIOHandler.send("connected");
            socketIOHandler.getInputStream()
                    .peek(gameCommandController)
                    .filter(command -> command.equals("EXIT"))
                    .findAny();

            gameServiceThreadLocal.remove();
            socketChannelRegistry.remove();
            LOGGER.debug("Socket listener shutting down");
        } catch (Exception ex) {
            LOGGER.error("Socket listener exited with this exception", ex);
        }
    }
}

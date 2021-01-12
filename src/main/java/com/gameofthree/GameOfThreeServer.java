package com.gameofthree;

import com.gameofthree.game.service.Game;
import com.gameofthree.game.service.GameFactory;
import com.gameofthree.game.service.GameService;
import com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.DivideByThreeLogic;
import com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.IGameTurnLogic;
import com.gameofthree.game.service.gameturn.gamerules.gameplaylogic.validator.DivideByThreeValidator;
import com.gameofthree.game.service.gameturn.gamerules.gamewinlogic.GameWinLogic;
import com.gameofthree.game.service.gameturn.gamerules.gamewinlogic.IGameWinLogic;
import com.gameofthree.server.GameServer;
import com.gameofthree.server.sockets.ServerSocketListener;
import com.gameofthree.server.sockets.SocketChannelRegistry;
import com.gameofthree.utils.PropertiesConfigLoader;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GameOfThreeServer {

    static {
        initializeGlobalConfiguration();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GameOfThreeServer.class);
    private static final String SERVER_LISTENERS_COUNT = PropertiesConfigLoader.getProperties().getProperty("server_listeners_count");
    private static final String PORT = PropertiesConfigLoader.getProperties().getProperty("port");



    public static void main(String[] args) throws IOException {

        LOGGER.info("The game has been started");

        try (GameServer gameServer = new GameServer(Integer.parseInt(PORT))) {
            ServerSocket serverSocket = gameServer.start();
            runServerListenerThreads(serverSocket);
        }

        LOGGER.info("The Game has been ended / closed");
    }

    private static void runServerListenerThreads(ServerSocket mainServerSocket) {
        int serverListenersCount = Integer.parseInt(SERVER_LISTENERS_COUNT);
        ExecutorService executorService = new ThreadPoolExecutor(
                serverListenersCount, serverListenersCount, 0, TimeUnit.SECONDS, new LinkedBlockingDeque<>());

        //compose global game
        GameService gameManager = new GameService(composeGame());

        SocketChannelRegistry socketChannelRegistry = new SocketChannelRegistry();

        ThreadLocal<GameService> gameService = InheritableThreadLocal.withInitial(() -> gameManager);
        ThreadLocal<SocketChannelRegistry> globalSocketChannelRegistry = InheritableThreadLocal.withInitial(() -> socketChannelRegistry);

        for (int i = 0; i < serverListenersCount; i++) {
            executorService.execute(new ServerSocketListener(mainServerSocket, globalSocketChannelRegistry, gameService));
        }
        executorService.shutdown();

        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("InterruptedException while running main thread.", e);
        }
    }

    private static Game composeGame() {
        IGameTurnLogic gameLogic = new DivideByThreeLogic();
        gameLogic.addValidator(new DivideByThreeValidator());

        IGameWinLogic winLogic = new GameWinLogic();

        return new GameFactory(gameLogic, winLogic).buildNewGame();
    }

    private static void initializeGlobalConfiguration() {
        PropertyConfigurator.configure(PropertyConfigurator.class.getClassLoader().getResourceAsStream("log4j.properties"));
        PropertiesConfigLoader.initialize("application.properties");
    }
}

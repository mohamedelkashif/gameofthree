package com.gameofthree.server.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class ServerSocketListener implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSocketListener.class);
    private ServerSocket serverSocket;
    private ThreadLocal<GameService> gameServiceThreadLocal;


    @Override
    public void run() {

    }
}

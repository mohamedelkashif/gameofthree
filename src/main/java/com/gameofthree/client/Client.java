package com.gameofthree.client;

import com.gameofthree.game.exceptions.ConnectionException;
import com.gameofthree.server.sockets.SocketIOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    private Socket clientSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;


    public SocketIOHandler start(String ip, int port) {
        LOGGER.info("Connecting to ip: {}, port: {}.", ip, port);
        try {
            clientSocket = new Socket(ip, port);
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            stop();
        }
        return new SocketIOHandler(bufferedReader, printWriter);
    }

    public void stop() {
        LOGGER.info("Stopping client.");
        try {
            bufferedReader.close();
            printWriter.close();
            clientSocket.close();
            LOGGER.debug("Client stopped.");
        } catch (IOException ex) {
            throw new ConnectionException(ex.getMessage());
        }
    }
}

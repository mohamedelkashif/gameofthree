package com.gameofthree.server.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerStream implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStream.class);

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public ServerStream(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public SocketIOHandler start() throws IOException {
        LOGGER.debug("Starting streaming");
        try {
            clientSocket = serverSocket.accept();
            printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            LOGGER.debug("Streaming started");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            close();
        }
        return new SocketIOHandler(bufferedReader, printWriter);
    }
    @Override
    public void close() throws IOException {
        LOGGER.info("Stopping server");
        try {
            bufferedReader.close();
            printWriter.close();
            clientSocket.close();
            LOGGER.debug("Streaming stopped");
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}

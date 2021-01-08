package com.gameofthree.server.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class SocketIOHandler implements ISocketIOHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketIOHandler.class);
    private static final int SEND_WITH_DELAY_VALUE = 75;
    private Collection<SocketIOHandler> allActiveSocketChannels = new CopyOnWriteArrayList<>();

    private BufferedReader bufferedReader;
    private PrintWriter printWriter;

    public SocketIOHandler(BufferedReader bufferedReader, PrintWriter printWriter) {
        this.bufferedReader = bufferedReader;
        this.printWriter = printWriter;
    }

    @Override
    public void send(String message) {
        LOGGER.debug("Sending message: {}", message);
        printWriter.println(message);
    }

    @Override
    public void sndWithDelay(String message) {
        send(message);
        try{
            Thread.sleep(SEND_WITH_DELAY_VALUE);
        }catch (InterruptedException exception){
            throw new RuntimeException(exception);

        }

    }

    @Override
    public void broadcast(String message) {
        allActiveSocketChannels
                .forEach((socketIOHandler -> socketIOHandler.send(message)));
    }

    @Override
    public Stream<String> getInputStream() {
        return getValidInputStream()
                .peek(message -> LOGGER.info("Received command: {}", message));

    }

    @Override
    public String readNextLineSync() {
        return getValidInputStream()
                .peek(command -> LOGGER.info("Received command: {}", command))
                .findFirst()
                .orElse("Stream closed");
    }

    @Override
    public boolean inputIsEmpty() {
        try{
           return  !bufferedReader.ready();
        }catch (IOException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public void clearInput() {
        try{
            Thread.sleep(100);
            while (!inputIsEmpty()){
                bufferedReader.read();
            }
        }catch (IOException | InterruptedException exception){
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public void setActiveSocketChannels(Collection<SocketIOHandler> allActiveSocketChannels) {
        this.allActiveSocketChannels = allActiveSocketChannels;

    }

    private Stream<String> getValidInputStream() {
        return bufferedReader.lines()
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketIOHandler that = (SocketIOHandler) o;
        return Objects.equals(bufferedReader, that.bufferedReader) &&
                Objects.equals(printWriter, that.printWriter) &&
                Objects.equals(allActiveSocketChannels, that.allActiveSocketChannels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bufferedReader, printWriter, allActiveSocketChannels);
    }
}

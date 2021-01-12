package com.gameofthree.server.sockets;

import java.util.Collection;
import java.util.stream.Stream;

public interface ISocketIOHandler {

    void send(String message);

    void sendWithDelay(String message);

    void broadcast(String message);

    Stream<String> getInputStream();

    String readNextLineSync();

    boolean inputIsEmpty();

    void clearInput();

    void setActiveSocketChannels(Collection<SocketIOHandler> allActiveSocketChannels);

}

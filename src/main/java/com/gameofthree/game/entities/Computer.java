package com.gameofthree.game.entities;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Computer implements IPlayer{

    private static final String COMPUTER_NAME = "Computer";
    private static final AtomicInteger nameCounter = new AtomicInteger(1);



    private final String id;
    private final String name;

    public Computer(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Computer generate(){
        return new Computer(UUID.randomUUID().toString(), COMPUTER_NAME + nameCounter.incrementAndGet());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSame(IPlayer player) {
        return this.getId().equals(player.getId());
    }

    @Override
    public boolean isComputer() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Computer player = (Computer) o;
        return Objects.equals(id, player.id) &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("computer ")
                .append(name);
        return sb.toString();
    }
}

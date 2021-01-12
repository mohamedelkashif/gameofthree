package com.gameofthree.game.entities;

import java.util.Objects;

public class Human implements IPlayer {

    public static final Human NULL = new Human("-", "unknown");

    private final String id;
    private final String name;

    public Human(final String id, final String name) {
        this.id = id;
        this.name = name;
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
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Human player = (Human) o;
        return Objects.equals(id, player.id) &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "player " + name;
    }

}

package com.gameofthree.game.entities;

import com.gameofthree.utils.PropertiesConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerAggregate implements Iterator<PlayerAggregate> {

    private static final int DEFAULT_ROOT_INDEX = Integer.parseInt(PropertiesConfigLoader.getProperties()
            .getProperty("index_of_player_that_starts_first", "0"));

    private static final int PLAYER_COUNT = Integer.parseInt(PropertiesConfigLoader.getProperties()
            .getProperty("player_count"));

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerAggregate.class);

    public static final PlayerAggregate NULL = new PlayerAggregate(Collections.emptyList(), DEFAULT_ROOT_INDEX);

    private final List<IPlayer> players;
    private final int rootIndex;

    public PlayerAggregate(List<IPlayer> players, int rootIndex) {
        this.players = Collections.unmodifiableList(players);
        this.rootIndex = rootIndex;
    }

    public IPlayer getRootPlayer() {
        return Optional.of(rootIndex)
                .filter(this::isValidRootIndex)
                .map(players::get)
                .orElse(Human.NULL);
    }

    public PlayerAggregate addPlayer(final IPlayer player) {
        List<IPlayer> newList = new ArrayList<>(players);
        newList.add(player);
        return new PlayerAggregate(Collections.unmodifiableList(newList), rootIndex);
    }

    public PlayerAggregate removePlayer(IPlayer player) {
        List<IPlayer> newList = players.stream()
                .filter(p -> !p.isSame(player))
                .collect(Collectors.toList());
        return new PlayerAggregate(Collections.unmodifiableList(newList), rootIndex);
    }


    public boolean isValid() {
        return players.size() == PLAYER_COUNT &&
                isValidRootIndex(rootIndex);
    }


    public boolean hasPlayer(IPlayer player) {
        return players.stream()
                .anyMatch(p -> p.isSame(player));
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public PlayerAggregate next() {
        return new PlayerAggregate(players, getNextRootIndex());
    }


    private int getNextRootIndex() {
        try {
            return (rootIndex + 1) % players.size();
        } catch(ArithmeticException e) {
            LOGGER.error("Can not divide by zero.");
        }
        return -1;
    }

    private boolean isValidRootIndex(int index) {
        return index >= 0 &&
                index < players.size();
    }

    public boolean acceptsMorePlayers() {
        return players.size() < PLAYER_COUNT;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAggregate that = (PlayerAggregate) o;
        return rootIndex == that.rootIndex &&
                Objects.equals(players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players, rootIndex);
    }

    @Override
    public String toString() {
        return "players: " +
                players +
                " and player " +
                (rootIndex + 1) +
                " has next turn.";
    }
}

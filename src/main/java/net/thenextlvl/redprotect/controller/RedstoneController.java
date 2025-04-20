package net.thenextlvl.redprotect.controller;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface RedstoneController<T> {
    int getMaxUpdates();

    int getState(T region);

    int setState(T region, int state);

    boolean isBlocked(T region);

    void setBlocked(T region, boolean blocked);

    default int decreaseState(T region) {
        return setState(region, getState(region) - 1);
    }

    default int increaseState(T region) {
        return setState(region, getState(region) + 1);
    }

    void startTransaction(Location location, BiConsumer<T, Location> transaction);

    String toString(T region);

    Optional<Player> getOwner(T region);
}

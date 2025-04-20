package net.thenextlvl.redprotect.api;

import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

public abstract class AbstractRedstoneController<T> implements RedstoneController<T> {
    private final Map<T, Integer> states = new WeakHashMap<>();
    private final Set<T> blocked = new HashSet<>();
    private final int maxUpdates;

    protected final RedProtect plugin;

    protected AbstractRedstoneController(RedProtect plugin) {
        this.maxUpdates = plugin.config.updatesPerState();
        this.plugin = plugin;
    }

    @Override
    public int getMaxUpdates() {
        return maxUpdates;
    }

    @Override
    public int getState(T region) {
        return states.getOrDefault(region, 0);
    }

    @Override
    public int setState(T region, int state) {
        if (state <= 0) states.remove(region);
        else states.put(region, state);
        return state;
    }

    @Override
    public boolean isBlocked(T region) {
        return blocked.contains(region);
    }

    @Override
    public void setBlocked(T region, boolean blocked) {
        if (blocked) this.blocked.add(region);
        else this.blocked.remove(region);
    }

    @Override
    public void startTransaction(Location location, BiConsumer<T, Location> transaction) {
        transaction.accept(getRegion(location), location);
    }

    protected abstract T getRegion(Location location);
}

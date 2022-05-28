package net.nonswag.tnl.redprotect.listeners;

import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RedstoneListener implements Listener {

    @Nonnull
    private static final HashMap<Location, Integer> states = new HashMap<>();
    @Nonnull
    private static final List<Location> sources = new ArrayList<>();

    @EventHandler
    public void onRedstone(@Nonnull BlockRedstoneEvent event) {
        if (RedProtect.getInstance().isRedstone()) {
            Location location = event.getBlock().getLocation();
            if (increaseState(location) >= 10) {
                if (!sources.contains(location)) {
                    RedProtect.getInstance().broadcastMalicious(location);
                    sources.add(location);
                    Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> sources.remove(location), 100);
                }
                event.setNewCurrent(0);
            }
            Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> decreaseState(location), 100);
        } else event.setNewCurrent(0);
    }

    public static int increaseState(@Nonnull Location location) {
        return setState(location, getState(location) + 1);
    }

    public static int decreaseState(@Nonnull Location location) {
        return setState(location, getState(location) - 1);
    }

    public static int setState(@Nonnull Location location, int state) {
        if (state <= 0) states.remove(location);
        else states.put(location, state);
        return getState(location);
    }

    public static int getState(@Nonnull Location location) {
        return states.getOrDefault(location, 0);
    }
}

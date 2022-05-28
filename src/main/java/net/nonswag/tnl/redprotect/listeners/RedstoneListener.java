package net.nonswag.tnl.redprotect.listeners;

import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class RedstoneListener implements Listener {

    @Nonnull
    private static final HashMap<Location, Integer> states = new HashMap<>();

    @EventHandler
    public void onRedstone(@Nonnull BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();
        if (increaseState(location) <= 5) return;
        event.setNewCurrent(0);
        Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> decreaseState(location), 20);
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

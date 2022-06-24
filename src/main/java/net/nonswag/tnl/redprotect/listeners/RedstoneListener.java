package net.nonswag.tnl.redprotect.listeners;

import com.plotsquared.core.plot.Plot;
import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RedstoneListener implements Listener {

    @Nonnull
    private static final HashMap<Chunk, Integer> CHUNK_STATES = new HashMap<>();
    @Nonnull
    private static final HashMap<Plot, Integer> PLOT_STATES = new HashMap<>();
    @Nonnull
    private static final List<Chunk> BLOCKED_CHUNKS = new ArrayList<>();
    @Nonnull
    private static final List<Plot> BLOCKED_PLOTS = new ArrayList<>();

    @EventHandler
    public void onRedstone(@Nonnull BlockRedstoneEvent event) {
        if (!RedProtect.getInstance().isRedstone()) event.setNewCurrent(0);
        if (event.getNewCurrent() == 0) return;
        Location location = event.getBlock().getLocation();
        Plot plot = plot(location);
        if (plot != null) {
            Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> decreaseState(plot), 300);
            if (increaseState(plot) < 5000) return;
            event.setNewCurrent(0);
            if (BLOCKED_PLOTS.contains(plot)) return;
            RedProtect.getInstance().broadcastMalicious(location, plot);
            BLOCKED_PLOTS.add(plot);
            Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> BLOCKED_PLOTS.remove(plot), 300);
        } else {
            Chunk chunk = location.getChunk();
            Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> decreaseState(chunk), 300);
            if (increaseState(chunk) < 5000) return;
            event.setNewCurrent(0);
            if (BLOCKED_CHUNKS.contains(chunk)) return;
            RedProtect.getInstance().broadcastMalicious(location, null);
            BLOCKED_CHUNKS.add(chunk);
            Bukkit.getScheduler().runTaskLater(RedProtect.getInstance(), () -> BLOCKED_CHUNKS.remove(chunk), 300);
        }
    }

    public static int increaseState(@Nonnull Chunk chunk) {
        return setState(chunk, getState(chunk) + 1);
    }

    public static int decreaseState(@Nonnull Chunk chunk) {
        return setState(chunk, getState(chunk) - 1);
    }

    public static int setState(@Nonnull Chunk chunk, int state) {
        if (state <= 0) CHUNK_STATES.remove(chunk);
        else CHUNK_STATES.put(chunk, state);
        return getState(chunk);
    }

    public static int getState(@Nonnull Chunk chunk) {
        return CHUNK_STATES.getOrDefault(chunk, 0);
    }

    public static int increaseState(@Nonnull Plot plot) {
        return setState(plot, getState(plot) + 1);
    }

    public static int decreaseState(@Nonnull Plot plot) {
        return setState(plot, getState(plot) - 1);
    }

    public static int setState(@Nonnull Plot plot, int state) {
        if (state <= 0) PLOT_STATES.remove(plot);
        else PLOT_STATES.put(plot, state);
        return getState(plot);
    }

    public static int getState(@Nonnull Plot plot) {
        return PLOT_STATES.getOrDefault(plot, 0);
    }

    @Nullable
    private static Plot plot(@Nonnull Location location) {
        if (location.getWorld() == null) return null;
        return Plot.getPlot(com.plotsquared.core.location.Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}

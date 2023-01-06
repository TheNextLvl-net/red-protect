package net.nonswag.tnl.redprotect.listeners;

import com.plotsquared.core.plot.Plot;
import net.nonswag.core.api.annotation.FieldsAreNonnullByDefault;
import net.nonswag.core.api.annotation.MethodsReturnNullableByDefault;
import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNullableByDefault
public class RedstoneListener implements Listener {
    private static final HashMap<Chunk, Integer> CHUNK_STATES = new HashMap<>();
    private static final HashMap<Plot, Integer> PLOT_STATES = new HashMap<>();
    private static final List<Chunk> BLOCKED_CHUNKS = new ArrayList<>();
    private static final List<Plot> BLOCKED_PLOTS = new ArrayList<>();

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event) {
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

    public static int increaseState(Chunk chunk) {
        return setState(chunk, getState(chunk) + 1);
    }

    public static int decreaseState(Chunk chunk) {
        return setState(chunk, getState(chunk) - 1);
    }

    public static int setState(Chunk chunk, int state) {
        if (state <= 0) CHUNK_STATES.remove(chunk);
        else CHUNK_STATES.put(chunk, state);
        return getState(chunk);
    }

    public static int getState(Chunk chunk) {
        return CHUNK_STATES.getOrDefault(chunk, 0);
    }

    public static int increaseState(Plot plot) {
        return setState(plot, getState(plot) + 1);
    }

    public static int decreaseState(Plot plot) {
        return setState(plot, getState(plot) - 1);
    }

    public static int setState(Plot plot, int state) {
        if (state <= 0) PLOT_STATES.remove(plot);
        else PLOT_STATES.put(plot, state);
        return getState(plot);
    }

    public static int getState(Plot plot) {
        return PLOT_STATES.getOrDefault(plot, 0);
    }

    private static Plot plot(Location location) {
        if (location.getWorld() == null) return null;
        return Plot.getPlot(com.plotsquared.core.location.Location.at(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}

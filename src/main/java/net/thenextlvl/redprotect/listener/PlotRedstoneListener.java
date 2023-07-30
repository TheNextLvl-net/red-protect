package net.thenextlvl.redprotect.listener;

import com.plotsquared.core.plot.Plot;
import lombok.RequiredArgsConstructor;
import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class PlotRedstoneListener implements Listener {
    private static final HashMap<Plot, Integer> PLOT_STATES = new HashMap<>();
    private static final List<Plot> BLOCKED_PLOTS = new ArrayList<>();

    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == 0) return;
        var location = event.getBlock().getLocation();
        var plot = plot(location);
        if (plot == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> decreaseState(plot), 300);
        if (increaseState(plot) < 5000) return;
        event.setNewCurrent(0);
        if (BLOCKED_PLOTS.contains(plot)) return;
        RedProtect.broadcastMalicious(location, plot);
        BLOCKED_PLOTS.add(plot);
        Bukkit.getScheduler().runTaskLater(plugin, () -> BLOCKED_PLOTS.remove(plot), 300);
    }

    public static int increaseState(Plot plot) {
        return setState(plot, getState(plot) + 1);
    }

    public static void decreaseState(Plot plot) {
        setState(plot, getState(plot) - 1);
    }

    public static int setState(Plot plot, int state) {
        if (state <= 0) PLOT_STATES.remove(plot);
        else PLOT_STATES.put(plot, state);
        return getState(plot);
    }

    public static int getState(Plot plot) {
        return PLOT_STATES.getOrDefault(plot, 0);
    }

    private static @Nullable Plot plot(Location location) {
        if (location.getWorld() == null) return null;
        return Plot.getPlot(com.plotsquared.core.location.Location.at(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ));
    }
}

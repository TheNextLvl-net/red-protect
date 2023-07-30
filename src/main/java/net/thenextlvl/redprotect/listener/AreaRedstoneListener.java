package net.thenextlvl.redprotect.listener;

import lombok.RequiredArgsConstructor;
import net.thenextlvl.protect.area.Area;
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
public class AreaRedstoneListener implements Listener {
    private static final HashMap<Area, Integer> AREA_STATES = new HashMap<>();
    private static final List<Area> BLOCKED_AREAS = new ArrayList<>();

    private final JavaPlugin plugin;

    @EventHandler(priority = EventPriority.HIGH)
    public void onRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == 0) return;
        var location = event.getBlock().getLocation();
        var area = area(location);
        if (area == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> decreaseState(area), 300);
        if (increaseState(area) < 5000) return;
        event.setNewCurrent(0);
        if (BLOCKED_AREAS.contains(area)) return;
        RedProtect.broadcastMalicious(location, null);
        BLOCKED_AREAS.add(area);
        Bukkit.getScheduler().runTaskLater(plugin, () -> BLOCKED_AREAS.remove(area), 300);
    }

    public static int increaseState(Area area) {
        return setState(area, getState(area) + 1);
    }

    public static void decreaseState(Area area) {
        setState(area, getState(area) - 1);
    }

    public static int setState(Area area, int state) {
        if (state <= 0) AREA_STATES.remove(area);
        else AREA_STATES.put(area, state);
        return getState(area);
    }

    public static int getState(Area area) {
        return AREA_STATES.getOrDefault(area, 0);
    }

    private static @Nullable Area area(Location location) {
        return location.getWorld() != null ? Area.highestArea(location) : null;
    }
}

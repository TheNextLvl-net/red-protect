package net.thenextlvl.redprotect.listener;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaProvider;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class AreaRedstoneListener implements Listener {
    private static final Map<Area, Integer> AREA_STATES = new WeakHashMap<>();
    private static final List<Area> BLOCKED_AREAS = new ArrayList<>();
    private final AreaProvider areaProvider;

    private final JavaPlugin plugin;

    public AreaRedstoneListener(JavaPlugin plugin) {
        this.areaProvider = Objects.requireNonNull(plugin.getServer().getServicesManager().load(AreaProvider.class));
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == 0) return;
        var location = event.getBlock().getLocation();
        var area = area(location);
        if (area == null) return;
        var time = RedProtect.config.clockDisableTime();
        var updates = RedProtect.config.updatesPerState();
        Bukkit.getScheduler().runTaskLater(plugin, () -> decreaseState(area), time);
        if (increaseState(area) < updates) return;
        event.setNewCurrent(0);
        if (BLOCKED_AREAS.contains(area)) return;
        RedProtect.broadcastMalicious(location, null);
        BLOCKED_AREAS.add(area);
        Bukkit.getScheduler().runTaskLater(plugin, () -> BLOCKED_AREAS.remove(area), time);
    }

    public int increaseState(Area area) {
        return setState(area, getState(area) + 1);
    }

    public void decreaseState(Area area) {
        setState(area, getState(area) - 1);
    }

    public int setState(Area area, int state) {
        if (state <= 0) AREA_STATES.remove(area);
        else AREA_STATES.put(area, state);
        return getState(area);
    }

    public int getState(Area area) {
        return AREA_STATES.getOrDefault(area, 0);
    }

    private @Nullable Area area(Location location) {
        return location.getWorld() != null ? areaProvider.getArea(location) : null;
    }
}

package net.thenextlvl.redprotect.listener;

import net.thenextlvl.redprotect.RedProtect;
import net.thenextlvl.redprotect.api.RedstoneController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.concurrent.TimeUnit;

public class RegionRedstoneListener<T> implements Listener {
    private final RedstoneController<T> controller;
    private final RedProtect plugin;

    public RegionRedstoneListener(RedProtect plugin, RedstoneController<T> controller) {
        this.controller = controller;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == event.getOldCurrent()) return;
        controller.startTransaction(event.getBlock().getLocation(), (area, location) -> {
            var time = plugin.config.clockDisableTime();

            plugin.getServer().getAsyncScheduler().runDelayed(plugin, task ->
                    controller.decreaseState(area), time, TimeUnit.MILLISECONDS);

            if (controller.increaseState(area) < controller.getMaxUpdates()) return;
            event.setNewCurrent(event.getOldCurrent());

            if (controller.isBlocked(area)) return;
            plugin.broadcastMalicious(location, null);

            controller.setBlocked(area, true);
            plugin.getServer().getAsyncScheduler().runDelayed(plugin, task ->
                    controller.setBlocked(area, false), time, TimeUnit.MILLISECONDS);
        });
    }
}

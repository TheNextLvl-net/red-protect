package net.thenextlvl.redprotect.listener;

import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener implements Listener {
    private final RedProtect plugin;

    public RedstoneListener(RedProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (!plugin.redstone) event.setNewCurrent(event.getOldCurrent());
    }
}

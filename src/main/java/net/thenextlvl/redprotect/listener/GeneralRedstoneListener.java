package net.thenextlvl.redprotect.listener;

import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class GeneralRedstoneListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (!RedProtect.redstone) event.setNewCurrent(0);
    }
}

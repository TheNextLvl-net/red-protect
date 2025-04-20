package net.thenextlvl.redprotect.listener;

import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChunkRedstoneListener implements Listener {
    private static final HashMap<Chunk, Integer> CHUNK_STATES = new HashMap<>();
    private static final List<Chunk> BLOCKED_CHUNKS = new ArrayList<>();

    private final RedProtect plugin;

    public ChunkRedstoneListener(RedProtect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRedstone(BlockRedstoneEvent event) {
        if (event.getNewCurrent() == 0) return;
        var location = event.getBlock().getLocation();
        Chunk chunk = location.getChunk();
        var time = plugin.config.clockDisableTime();
        var updates = plugin.config.updatesPerState();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> decreaseState(chunk), time);
        if (increaseState(chunk) < updates) return;
        event.setNewCurrent(0);
        if (BLOCKED_CHUNKS.contains(chunk)) return;
        plugin.broadcastMalicious(location, null);
        BLOCKED_CHUNKS.add(chunk);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> BLOCKED_CHUNKS.remove(chunk), time);
    }

    public static int increaseState(Chunk chunk) {
        return setState(chunk, getState(chunk) + 1);
    }

    public static void decreaseState(Chunk chunk) {
        setState(chunk, getState(chunk) - 1);
    }

    public static int setState(Chunk chunk, int state) {
        if (state <= 0) CHUNK_STATES.remove(chunk);
        else CHUNK_STATES.put(chunk, state);
        return getState(chunk);
    }

    public static int getState(Chunk chunk) {
        return CHUNK_STATES.getOrDefault(chunk, 0);
    }
}

package net.thenextlvl.redprotect.controller;

import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Chunk;
import org.bukkit.Location;

public class ChunkRedstoneController extends AbstractRedstoneController<Chunk> {
    public ChunkRedstoneController(RedProtect plugin) {
        super(plugin);
    }

    @Override
    protected Chunk getRegion(Location location) {
        return location.getChunk();
    }
}

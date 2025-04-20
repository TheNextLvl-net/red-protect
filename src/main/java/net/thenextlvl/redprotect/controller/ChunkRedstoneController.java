package net.thenextlvl.redprotect.controller;

import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ChunkRedstoneController extends AbstractRedstoneController<Chunk> {
    public ChunkRedstoneController(RedProtect plugin) {
        super(plugin);
    }

    @Override
    public String toString(Chunk chunk) {
        return chunk.getX() + ", " + chunk.getZ();
    }

    @Override
    public Optional<Player> getOwner(Chunk region) {
        return Optional.empty();
    }

    @Override
    protected Chunk getRegion(Location location) {
        return location.getChunk();
    }
}

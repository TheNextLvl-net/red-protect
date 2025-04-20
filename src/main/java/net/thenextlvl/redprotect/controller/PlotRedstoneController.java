package net.thenextlvl.redprotect.controller;

import com.plotsquared.core.plot.Plot;
import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PlotRedstoneController extends AbstractRedstoneController<Plot> {
    public PlotRedstoneController(RedProtect plugin) {
        super(plugin);
    }

    @Override
    public String toString(Plot region) {
        return "";
    }

    @Override
    public Optional<Player> getOwner(Plot plot) {
        return Optional.ofNullable(plot.getOwner()).map(plugin.getServer()::getPlayer);
    }

    @Override
    protected Plot getRegion(Location location) {
        return Plot.getPlot(com.plotsquared.core.location.Location.at(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        ));
    }
}

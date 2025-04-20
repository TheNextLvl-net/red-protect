package net.thenextlvl.redprotect.api;

import com.plotsquared.core.plot.Plot;
import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Location;

public class PlotRedstoneController extends AbstractRedstoneController<Plot> {
    public PlotRedstoneController(RedProtect plugin) {
        super(plugin);
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

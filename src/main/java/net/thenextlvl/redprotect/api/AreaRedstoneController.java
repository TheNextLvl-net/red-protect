package net.thenextlvl.redprotect.api;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaProvider;
import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Location;

import java.util.Objects;

public class AreaRedstoneController extends AbstractRedstoneController<Area> {
    private final AreaProvider areaProvider;

    public AreaRedstoneController(RedProtect plugin) {
        super(plugin);
        this.areaProvider = Objects.requireNonNull(plugin.getServer().getServicesManager().load(AreaProvider.class));
    }

    @Override
    protected Area getRegion(Location location) {
        return areaProvider.getArea(location);
    }
}

package net.thenextlvl.redprotect.controller;

import net.thenextlvl.protect.area.Area;
import net.thenextlvl.protect.area.AreaProvider;
import net.thenextlvl.redprotect.RedProtect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public class AreaRedstoneController extends AbstractRedstoneController<Area> {
    private final AreaProvider areaProvider;

    public AreaRedstoneController(RedProtect plugin) {
        super(plugin);
        this.areaProvider = Objects.requireNonNull(plugin.getServer().getServicesManager().load(AreaProvider.class));
    }

    @Override
    public String toString(Area area) {
        return area.getName();
    }

    @Override
    public Optional<Player> getOwner(Area area) {
        return area.getOwner().map(plugin.getServer()::getPlayer);
    }

    @Override
    public void notifyOwner(Area area, BiConsumer<Player, String> notification) {
        getOwner(area).ifPresent(player -> notification.accept(player, "redstone.disabled.area"));
    }

    @Override
    protected Area getRegion(Location location) {
        return areaProvider.getArea(location);
    }
}

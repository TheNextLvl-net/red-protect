package net.thenextlvl.redprotect;

import com.plotsquared.core.plot.Plot;
import core.file.format.GsonFile;
import core.io.IO;
import net.thenextlvl.redprotect.api.AreaRedstoneController;
import net.thenextlvl.redprotect.api.ChunkRedstoneController;
import net.thenextlvl.redprotect.api.PlotRedstoneController;
import net.thenextlvl.redprotect.listener.RedstoneListener;
import net.thenextlvl.redprotect.listener.RegionRedstoneListener;
import net.thenextlvl.redprotect.util.Config;
import net.thenextlvl.redprotect.util.Messages;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class RedProtect extends JavaPlugin {
    public final Config config = new GsonFile<>(IO.of(getDataFolder(), "config.json"), new Config(
            true, false, true, true, 18, TimeUnit.SECONDS.toMillis(5), TimeUnit.SECONDS.toMillis(15), 5000
    )).saveIfAbsent().getRoot();
    public boolean redstone = true;

    @Override
    public void onEnable() {
        registerListeners();
        if (config.lagDisableRedstone()) getServer().getAsyncScheduler().runAtFixedRate(this, task -> {
            if (getServer().getTPS()[0] <= config.disableRedstoneTPS() && redstone) {
                redstone = false;
                broadcastMeasure();
            } else if (getServer().getTPS()[0] > config.disableRedstoneTPS() && !redstone) {
                redstone = true;
                broadcastMeasure();
            }
        }, config.lagDetectInterval(), config.lagDetectInterval(), TimeUnit.MILLISECONDS);
    }

    public void registerListeners() {
        if (config.chunkProtection())
            registerListener(new RegionRedstoneListener<>(this, new ChunkRedstoneController(this)));
        if (config.areaProtection() && getServer().getPluginManager().getPlugin("Protect") != null)
            registerListener(new RegionRedstoneListener<>(this, new AreaRedstoneController(this)));
        if (config.plotProtection() && getServer().getPluginManager().getPlugin("PlotSquared") != null)
            registerListener(new RegionRedstoneListener<>(this, new PlotRedstoneController(this)));
        registerListener(new RedstoneListener(this));
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @SuppressWarnings("unchecked")
    public void broadcastMeasure() {
        getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("redclock.notify"))
                .forEach(player -> {
                    var tps = Placeholder.<CommandSender>of("tps", config.disableRedstoneTPS());
                    if (redstone) player.sendRichMessage(Messages.REDSTONE_ENABLED.message(player.locale(), tps));
                    else player.sendRichMessage(Messages.REDSTONE_DISABLED.message(player.locale(), tps));
                });
    }

    @SuppressWarnings("unchecked")
    public void broadcastMalicious(Location location, @Nullable Plot plot) {
        var position = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
        var plotPosition = plot != null ? plot.getAlias().isEmpty() ? "Plot " + plot.getId() : plot.getAlias() : position;
        var placeholder = Placeholder.<CommandSender>of("plot", plotPosition);
        getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("redclock.notify") || redstone)
                .forEach(player -> {
                    var positionPlaceholder = Placeholder.<CommandSender>of("position", position);
                    player.sendRichMessage(Messages.DETECTED_REDSTONE_CLOCK.message(player.locale(),
                            placeholder, positionPlaceholder));
                    player.sendRichMessage(Messages.CLICK_TO_TELEPORT.message(player.locale(), positionPlaceholder));
                });
        if (plot == null || plot.getOwner() == null) return;
        var player = getServer().getPlayer(plot.getOwner());
        if (player == null) return;
        player.sendRichMessage(Messages.REDSTONE_DISABLED_PLOT.message(player.locale(), placeholder));
    }
}

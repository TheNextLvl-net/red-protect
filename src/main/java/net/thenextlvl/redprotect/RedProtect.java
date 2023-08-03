package net.thenextlvl.redprotect;

import com.plotsquared.core.plot.Plot;
import core.api.file.format.GsonFile;
import core.api.placeholder.Placeholder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.thenextlvl.redprotect.listener.AreaRedstoneListener;
import net.thenextlvl.redprotect.listener.ChunkRedstoneListener;
import net.thenextlvl.redprotect.listener.GeneralRedstoneListener;
import net.thenextlvl.redprotect.listener.PlotRedstoneListener;
import net.thenextlvl.redprotect.util.Config;
import net.thenextlvl.redprotect.util.Messages;
import net.thenextlvl.redprotect.util.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class RedProtect extends JavaPlugin {
    @Getter
    @Setter
    private static boolean redstone = true;
    @Getter
    @Accessors(fluent = true)
    private static final Config config = new GsonFile<>(
            new File("plugins/RedProtect/config.json"),
            new Config(true, 18, 100, 300, 5000)
    ) {{
        if (!getFile().exists()) save();
    }}.getRoot();

    @Override
    public void onLoad() {
        Placeholders.init();
    }

    @Override
    public void onEnable() {
        var manager = Bukkit.getPluginManager();
        manager.registerEvents(new GeneralRedstoneListener(), this);
        manager.registerEvents(new ChunkRedstoneListener(this), this);
        if (manager.isPluginEnabled("Protect"))
            manager.registerEvents(new AreaRedstoneListener(this), this);
        if (manager.isPluginEnabled("PlotSquared"))
            manager.registerEvents(new PlotRedstoneListener(this), this);

        if (config.lagDisableRedstone()) Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (Bukkit.getTPS()[0] <= config.disableRedstoneTPS() && isRedstone()) {
                setRedstone(false);
                broadcastMeasure();
            } else if (Bukkit.getTPS()[0] > config.disableRedstoneTPS() && !isRedstone()) {
                setRedstone(true);
                broadcastMeasure();
            }
        }, config.lagDetectInterval(), config.lagDetectInterval());
    }

    @SuppressWarnings("unchecked")
    public static void broadcastMeasure() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("redclock.notify"))
                .forEach(player -> {
                    var tps = Placeholder.<CommandSender>of("tps", config.disableRedstoneTPS());
                    if (isRedstone()) player.sendRichMessage(Messages.REDSTONE_ENABLED.message(player.locale(), tps));
                    else player.sendRichMessage(Messages.REDSTONE_DISABLED.message(player.locale(), tps));
                });
    }

    @SuppressWarnings("unchecked")
    public static void broadcastMalicious(Location location, @Nullable Plot plot) {
        var position = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
        var plotPosition = plot != null ? plot.getAlias().isEmpty() ? "Plot " + plot.getId() : plot.getAlias() : position;
        var placeholder = Placeholder.<CommandSender>of("plot", plotPosition);
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("redclock.notify") || isRedstone())
                .forEach(player -> {
                    var positionPlaceholder = Placeholder.<CommandSender>of("position", position);
                    player.sendRichMessage(Messages.DETECTED_REDSTONE_CLOCK.message(player.locale(),
                            placeholder, positionPlaceholder));
                    player.sendRichMessage(Messages.CLICK_TO_TELEPORT.message(player.locale(), positionPlaceholder));
                });
        if (plot == null || plot.getOwner() == null) return;
        var player = Bukkit.getPlayer(plot.getOwner());
        if (player == null) return;
        player.sendRichMessage(Messages.REDSTONE_DISABLED_PLOT.message(player.locale(), placeholder));
    }
}

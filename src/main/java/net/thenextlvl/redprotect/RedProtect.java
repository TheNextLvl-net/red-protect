package net.thenextlvl.redprotect;

import com.plotsquared.core.plot.Plot;
import core.file.format.GsonFile;
import core.i18n.file.ComponentBundle;
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

    private final File translations = new File(getDataFolder(), "translations");
    private final ComponentBundle bundle = new ComponentBundle(translations, audience ->
            audience instanceof Player player ? player.locale() : Locale.US)
            .register("redprotect", Locale.US)
            .register("redprotect_german", Locale.GERMANY)
            .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                    TagResolver.standard(),
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix"))
            )).build());
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

    private void broadcastMeasure(boolean enabled) {
        var message = enabled ? "redstone.enabled" : "redstone.disabled";
        var resolver = Formatter.number("tps", config.disableRedstoneTPS());
        getServer().getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("redclock.notify"))
                .forEach(player -> bundle().sendMessage(player, message, resolver));
        if (config.printToConsole()) bundle().sendMessage(getServer().getConsoleSender(), message, resolver);
    }

    public ComponentBundle bundle() {
        return bundle;
    }
}

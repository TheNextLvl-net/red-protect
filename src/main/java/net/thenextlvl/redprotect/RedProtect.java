package net.thenextlvl.redprotect;

import core.file.format.GsonFile;
import core.i18n.file.ComponentBundle;
import core.io.IO;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.thenextlvl.redprotect.controller.AreaRedstoneController;
import net.thenextlvl.redprotect.controller.ChunkRedstoneController;
import net.thenextlvl.redprotect.controller.PlotRedstoneController;
import net.thenextlvl.redprotect.listener.RedstoneListener;
import net.thenextlvl.redprotect.listener.RegionRedstoneListener;
import net.thenextlvl.redprotect.model.PluginConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RedProtect extends JavaPlugin {
    public final PluginConfig config = new GsonFile<>(IO.of(getDataFolder(), "config.json"), new PluginConfig(
            true, true, true, true, true, 18, TimeUnit.SECONDS.toMillis(10), TimeUnit.SECONDS.toMillis(10), 250000
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
            if (getServer().getTPS()[0] <= config.disableRedstoneTPS() && redstone)
                broadcastMeasure(redstone = false);
            else if (getServer().getTPS()[0] > config.disableRedstoneTPS() && !redstone)
                broadcastMeasure(redstone = true);
        }, config.lagDetectInterval(), config.lagDetectInterval(), TimeUnit.MILLISECONDS);
    }

    private void registerListeners() {
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

package net.nonswag.tnl.redprotect;

import com.plotsquared.core.plot.Plot;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.nonswag.tnl.redprotect.listeners.RedstoneListener;
import net.nonswag.tnl.redprotect.tasks.TPSTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@Setter
public class RedProtect extends JavaPlugin {

    @Nullable
    private static RedProtect instance;
    private boolean redstone = true;

    public RedProtect() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(), this);
        TPSTask.getInstance().start();
    }

    @Override
    public void onDisable() {
        TPSTask.getInstance().interrupt();
    }

    public void broadcastMeasure() {
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (!all.hasPermission("redclock.notify")) return;
            if (isRedstone()) {
                all.sendMessage("§8[§4RedProtect§8] §aThe TPS went above 18");
                all.sendMessage("§8[§4RedProtect§8] §aRedstone does now work again");
            } else {
                all.sendMessage("§8[§4RedProtect§8] §cThe TPS dropped below 18");
                all.sendMessage("§8[§4RedProtect§8] §cTo prevent further lag redstone is disabled now");
            }
        });
    }

    public void broadcastMalicious(@Nonnull Location location, @Nullable Plot plot) {
        Bukkit.getOnlinePlayers().forEach(all -> {
            if (!all.hasPermission("redclock.notify") || !isRedstone()) return;
            String position = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
            all.sendMessage("§8[§4RedProtect§8] §cDetected a malicious redstone clock at §4" +
                    (plot != null ? plot.getAlias().isEmpty() ? "Plot " + plot.getId() : plot.getAlias() : position));
            all.spigot().sendMessage(new ComponentBuilder().append("§8[§4RedProtect§8] §c§nClick to teleport you to the position").
                    event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + position)).
                    event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to teleport"))).create());
        });
        if (plot == null || plot.getOwner() == null) return;
        Player player = Bukkit.getPlayer(plot.getOwner());
        if (player == null) return;
        player.sendMessage("§8[§4RedProtect§8] §cRedstone got temporarily disabled on your plot §8(§4%s§8)".
                formatted(plot.getAlias().isEmpty() ? plot.getId().toString() : plot.getAlias()));
    }

    @Nonnull
    public static RedProtect getInstance() {
        assert instance != null;
        return instance;
    }
}

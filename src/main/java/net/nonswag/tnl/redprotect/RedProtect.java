package net.nonswag.tnl.redprotect;

import com.plotsquared.core.plot.Plot;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.nonswag.core.api.annotation.FieldsAreNullableByDefault;
import net.nonswag.core.api.annotation.MethodsReturnNonnullByDefault;
import net.nonswag.core.api.message.Message;
import net.nonswag.core.api.message.Placeholder;
import net.nonswag.tnl.listener.Listener;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.api.plugin.TNLPlugin;
import net.nonswag.tnl.redprotect.listeners.RedstoneListener;
import net.nonswag.tnl.redprotect.tasks.TPSTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@Setter
@FieldsAreNullableByDefault
@MethodsReturnNonnullByDefault
public class RedProtect extends TNLPlugin {

    private static RedProtect instance;
    private boolean redstone = true;

    public RedProtect() {
        instance = this;
    }

    @Override
    public void enable() {
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(), this);
        TPSTask.getInstance().start();
    }

    @Override
    public void disable() {
        TPSTask.getInstance().interrupt();
    }

    public void broadcastMeasure() {
        Listener.getOnlinePlayers().forEach(all -> {
            if (!all.permissionManager().hasPermission("redclock.notify")) return;
            if (isRedstone()) {
                all.messenger().sendMessage("%prefix% §aThe TPS went above 18");
                all.messenger().sendMessage("%prefix% §aRedstone does now work again");
            } else {
                all.messenger().sendMessage("%prefix% §cThe TPS dropped below 18");
                all.messenger().sendMessage("%prefix% §cTo prevent further lag redstone is disabled now");
            }
        });
    }

    public void broadcastMalicious(@Nonnull Location location, @Nullable Plot plot) {
        Listener.getOnlinePlayers().forEach(all -> {
            if (!all.permissionManager().hasPermission("redclock.notify") || !isRedstone()) return;
            String position = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
            all.messenger().sendMessage("%prefix% §cDetected a malicious redstone clock at §4" +
                    (plot != null ? plot.getAlias().isEmpty() ? "Plot " + plot.getId() : plot.getAlias() : position));
            all.bukkit().sendMessage(Component.text(Message.format("%prefix% §c§nClick to teleport you to the position")).
                    clickEvent(ClickEvent.runCommand("/tp " + position)).
                    hoverEvent(HoverEvent.showText(Component.text("§7Click to teleport"))));
        });
        if (plot == null || plot.getOwner() == null) return;
        TNLPlayer player = TNLPlayer.cast(plot.getOwner());
        if (player == null) return;
        player.messenger().sendMessage("%prefix% §cRedstone got temporarily disabled on your plot §8(§4%plot%§8)",
                new Placeholder("plot", plot.getAlias().isEmpty() ? plot.getId().toString() : plot.getAlias()));
    }

    public static RedProtect getInstance() {
        assert instance != null;
        return instance;
    }
}

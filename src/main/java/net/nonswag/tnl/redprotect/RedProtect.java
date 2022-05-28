package net.nonswag.tnl.redprotect;

import lombok.Getter;
import lombok.Setter;
import net.nonswag.tnl.redprotect.listeners.RedstoneListener;
import net.nonswag.tnl.redprotect.tasks.TPSTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Getter
@Setter
public class RedProtect extends JavaPlugin {

    @Nullable
    private static RedProtect instance;
    private boolean redstone = false;

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
                all.sendMessage("§8[§4RedProtect§8] §aThe TPS dropped below 18");
                all.sendMessage("§8[§4RedProtect§8] §cTo prevent further lag redstone is disabled now");
            }
        });
    }

    @Nonnull
    public static RedProtect getInstance() {
        assert instance != null;
        return instance;
    }
}

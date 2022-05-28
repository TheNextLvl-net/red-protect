package net.nonswag.tnl.redprotect;

import net.nonswag.tnl.redprotect.listeners.RedstoneListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RedProtect extends JavaPlugin {

    @Nullable
    private static RedProtect instance;

    public RedProtect() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(), this);
    }

    @Nonnull
    public static RedProtect getInstance() {
        assert instance != null;
        return instance;
    }
}

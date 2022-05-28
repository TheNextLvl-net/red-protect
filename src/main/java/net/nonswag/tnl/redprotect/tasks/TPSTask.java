package net.nonswag.tnl.redprotect.tasks;

import lombok.Getter;
import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;

import javax.annotation.Nonnull;

public class TPSTask extends Thread {

    @Getter
    @Nonnull
    private static final TPSTask instance = new TPSTask();

    @Override
    public void run() {
        try {
            while (isAlive() && !isInterrupted()) {
                Thread.sleep(5000);
                if (getTPS() <= 18 && RedProtect.getInstance().isRedstone()) {
                    RedProtect.getInstance().setRedstone(false);
                    RedProtect.getInstance().broadcastMeasure();
                } else if (getTPS() > 18 && !RedProtect.getInstance().isRedstone()) {
                    RedProtect.getInstance().setRedstone(true);
                    RedProtect.getInstance().broadcastMeasure();
                }
            }
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            System.err.println("An unexpected error occurred, please report this to a contributor");
            e.printStackTrace();
        }
    }

    private double getTPS() {
        return ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
    }
}

package net.nonswag.tnl.redprotect.tasks;

import lombok.Getter;
import net.nonswag.tnl.core.api.reflection.Reflection;
import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

public class TPSTask extends Thread {

    @Getter
    @Nonnull
    private static final TPSTask instance = new TPSTask();

    @Override
    public void run() {
        try {
            while (isAlive() && !isInterrupted()) {
                Thread.sleep(15000);
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
        return Reflection.<double[]>getField(Reflection.getField(Bukkit.getServer(), "console").nonnull(), "recentTps").nonnull()[0];
    }
}

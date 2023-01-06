package net.nonswag.tnl.redprotect.tasks;

import lombok.Getter;
import net.nonswag.core.api.annotation.FieldsAreNonnullByDefault;
import net.nonswag.tnl.redprotect.RedProtect;
import org.bukkit.Bukkit;

@FieldsAreNonnullByDefault
public class TPSTask extends Thread {

    @Getter
    private static final TPSTask instance = new TPSTask();

    @Override
    public void run() {
        try {
            while (isAlive() && !isInterrupted()) {
                Thread.sleep(5000);
                if (Bukkit.getTPS()[0] <= 18 && RedProtect.getInstance().isRedstone()) {
                    RedProtect.getInstance().setRedstone(false);
                    RedProtect.getInstance().broadcastMeasure();
                } else if (Bukkit.getTPS()[0] > 18 && !RedProtect.getInstance().isRedstone()) {
                    RedProtect.getInstance().setRedstone(true);
                    RedProtect.getInstance().broadcastMeasure();
                }
            }
        } catch (InterruptedException ignored) {
        }
    }
}

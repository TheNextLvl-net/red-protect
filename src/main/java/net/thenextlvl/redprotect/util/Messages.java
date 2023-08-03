package net.thenextlvl.redprotect.util;

import core.api.file.format.MessageFile;
import core.api.placeholder.MessageKey;
import core.api.placeholder.Placeholder;
import core.api.placeholder.SystemMessageKey;
import org.bukkit.command.CommandSender;

import java.util.Locale;

public class Messages {
    public static final Placeholder.Formatter<CommandSender> FORMATTER = new Placeholder.Formatter<>();

    public static final SystemMessageKey<CommandSender> PREFIX = new SystemMessageKey<>("redprotect-prefix", FORMATTER);

    public static final MessageKey<CommandSender> REDSTONE_ENABLED = new MessageKey<>("redstone-enabled", FORMATTER);
    public static final MessageKey<CommandSender> REDSTONE_DISABLED = new MessageKey<>("redstone-disabled", FORMATTER);
    public static final MessageKey<CommandSender> REDSTONE_DISABLED_PLOT = new MessageKey<>("redstone-disabled-plot", FORMATTER);
    public static final MessageKey<CommandSender> DETECTED_REDSTONE_CLOCK = new MessageKey<>("detected-redstone-clock", FORMATTER);
    public static final MessageKey<CommandSender> CLICK_TO_TELEPORT = new MessageKey<>("click-to-teleport", FORMATTER);

    static {
        initRoot();
        initEnglish();
        initGerman();
    }

    private static void initRoot() {
        var file = MessageFile.ROOT;
        file.setDefault(PREFIX, "<white>RedProtect <dark_gray>»<reset>");
        file.save();
    }

    private static void initEnglish() {
        var file = MessageFile.getOrCreate(Locale.forLanguageTag("en-US"));
        file.setDefault(REDSTONE_ENABLED, "%prefix% <white>The TPS went above <green>%tps%\n" +
                "%prefix% <white>Redstone is enabled again");
        file.setDefault(REDSTONE_DISABLED, "%prefix% <red>The TPS went below <dark_red>%tps%\n" +
                "%prefix% <red>To prevent further lag redstone is disabled now");
        file.setDefault(REDSTONE_DISABLED_PLOT, "%prefix% <red>Redstone got temporarily disabled on your plot " +
                "<dark_gray>(<dark_red>%plot%<dark_gray>)");
        file.setDefault(DETECTED_REDSTONE_CLOCK, "%prefix% <red>Detected a redstone clock at " +
                "<dark_red>%position%");
        file.setDefault(CLICK_TO_TELEPORT, "%prefix% <hover:show_text:\"<gray>Click to teleport\">" +
                "<click:run_command:\"/tp %position%\"><red>Click to teleport to the position");
        file.save();
    }

    private static void initGerman() {
        var file = MessageFile.getOrCreate(Locale.forLanguageTag("de-DE"));
        file.setDefault(REDSTONE_ENABLED, "%prefix% <white>Die TPS ist wieder über <green>%tps%\n" +
                "%prefix% <white>Redstone ist jetzt wieder aktiv");
        file.setDefault(REDSTONE_DISABLED, "%prefix% <red>Die TPS ist unter <dark_red>%tps%\n" +
                "%prefix% <red>Um weiteren Lag zu verhindern, ist Redstone jetzt deaktiviert");
        file.setDefault(REDSTONE_DISABLED_PLOT, "%prefix% <red>Redstone wurde temporär auf deinem plot " +
                "<dark_gray>(<dark_red>%plot%<dark_gray>) <red>deaktiviert");
        file.setDefault(DETECTED_REDSTONE_CLOCK, "%prefix% <red>Eine redstone clock wurde bei " +
                "<dark_red>%position% <red>entdeckt");
        file.setDefault(CLICK_TO_TELEPORT, "%prefix% <hover:show_text:\"<gray>Klicke zum teleportieren\">" +
                "<click:run_command:\"/tp %position%\"><red>Klicke um dich zu der position zu teleportieren");
        file.save();
    }
}

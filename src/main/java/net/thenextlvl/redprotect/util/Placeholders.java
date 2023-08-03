package net.thenextlvl.redprotect.util;

import core.api.placeholder.Placeholder;

public class Placeholders {
    public static void init() {
        Messages.FORMATTER.registry().register(Placeholder.of("prefix", Messages.PREFIX.message()));
    }
}

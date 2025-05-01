package net.thenextlvl.redprotect.model;

import com.google.gson.annotations.SerializedName;

public record PluginConfig(
        @SerializedName("enable-area-protection") boolean areaProtection,
        @SerializedName("enable-chunk-protection") boolean chunkProtection,
        @SerializedName("enable-plot-protection") boolean plotProtection,
        @SerializedName("print-messages-to-console") boolean printToConsole,
        @SerializedName("lag-disable-redstone") boolean lagDisableRedstone,
        @SerializedName("disable-clocks") boolean disableClocks,
        @SerializedName("disable-redstone-tps") int disableRedstoneTPS,
        @SerializedName("lag-detect-interval-millis") long lagDetectInterval,
        @SerializedName("clock-disable-time-millis") long clockDisableTime,
        @SerializedName("region-update-limit") int regionUpdateLimit
) {
}

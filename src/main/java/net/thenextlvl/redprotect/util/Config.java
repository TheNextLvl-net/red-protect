package net.thenextlvl.redprotect.util;

import com.google.gson.annotations.SerializedName;

public record Config(
        @SerializedName("lag-disable-redstone") boolean lagDisableRedstone,
        @SerializedName("disable-redstone-tps") int disableRedstoneTPS,
        @SerializedName("lag-detect-interval-millis") long lagDetectInterval,
        @SerializedName("clock-disable-time-millis") long clockDisableTime,
        @SerializedName("updates-per-state") int updatesPerState
) {
}

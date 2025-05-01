# RedProtect

RedProtect makes your server more robust against redstone clocks.</br>
To receive action messages the permission `redclock.notify` is required.</br>

## Features

- Plot based redclock protection
- Area based redclock protection
- Chunk based redclock protection
- TPS based redstone deactivation

## Supported Plugins

- [PlotSqaured](https://github.com/IntellectualSites/PlotSquared)
- [Protect](https://github.com/TheNextLvl-net/protect)

## Configuration

```json5
{
  "enable-area-protection": true, // whether area protection should be enabled
  "enable-chunk-protection": true, // whether chunk protection should be enabled
  "enable-plot-protection": true, // whether plotsquared protect should be enabled
  "print-messages-to-console": true, // whether clock notifications should be printed to console
  "lag-disable-redstone": true, // whether redstone should be disabled when the server lags
  "disable-redstone-tps": 18, // the tps when to disable redstone
  "lag-detect-interval-millis": 1000, // the interval of checking for lag
  "clock-disable-time-millis": 10000, // how long a clock should be disabled for
  "region-update-limit": 25000 // how many updates have to be detected before counted as clock
}
```
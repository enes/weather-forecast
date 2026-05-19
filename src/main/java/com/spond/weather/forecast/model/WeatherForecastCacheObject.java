package com.spond.weather.forecast.model;

import java.time.Instant;
import java.util.List;

public record WeatherForecastCacheObject(List<WeatherForecastEntry> entries,
                                         Instant lastUpdatedDate,
                                         Instant lastModifiedDate,
                                         Instant expires
) {
}

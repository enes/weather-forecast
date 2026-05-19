package com.spond.weather.forecast.model;

import java.time.Instant;
import java.util.List;

public record WeatherForecastResult(
        Instant updatedAt,
        Instant expires,
        Instant lastModified,
        List<WeatherForecastEntry> entries
) {
}
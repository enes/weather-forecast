package com.spond.weather.forecast.model;

import java.time.Instant;

public record WeatherForecastEntry(
        Instant forecastTime,
        double airTemperature,
        double windSpeed
) {
}

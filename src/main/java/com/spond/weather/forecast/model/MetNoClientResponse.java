package com.spond.weather.forecast.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MetNoClientResponse(Properties properties) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(
            Meta meta,
            List<Timeseries> timeseries
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
            @JsonProperty("updated_at")
            Instant updatedAt
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Timeseries(
            Instant time,
            ForecastData data
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForecastData(
            InstantForecast instant
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InstantForecast(
            ForecastDetails details
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ForecastDetails(
            @JsonProperty("air_temperature")
            Double airTemperature,

            @JsonProperty("wind_speed")
            Double windSpeed
    ) {
    }
}

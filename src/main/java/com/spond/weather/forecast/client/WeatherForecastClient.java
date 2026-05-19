package com.spond.weather.forecast.client;

import com.spond.weather.forecast.model.WeatherForecastResult;

import java.time.Instant;

public interface WeatherForecastClient {
    WeatherForecastResult getWeatherForecast(double latitude, double longitude, Instant lastModified);
}

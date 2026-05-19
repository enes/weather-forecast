package com.spond.weather.forecast.service;

import com.spond.weather.forecast.model.WeatherForecastCacheObject;

import java.time.Instant;

public interface InMemoryCache {
    WeatherForecastCacheObject getWeatherForecast(double lat, double lon, Instant startTime);
}

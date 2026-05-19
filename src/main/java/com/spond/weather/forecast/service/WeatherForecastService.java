package com.spond.weather.forecast.service;

import com.spond.weather.forecast.model.WeatherForecastResponse;

import java.time.Instant;

public interface WeatherForecastService {
    WeatherForecastResponse getWeatherForecast(Double lat, Double lon, Instant startTime);
}

package com.spond.weather.forecast.service.impl;

import com.spond.weather.forecast.model.WeatherForecastCacheObject;
import com.spond.weather.forecast.model.WeatherForecastEntry;
import com.spond.weather.forecast.model.WeatherForecastResponse;
import com.spond.weather.forecast.service.InMemoryCache;
import com.spond.weather.forecast.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private final InMemoryCache inMemoryCache;


    public WeatherForecastResponse getWeatherForecast(Double lat, Double lon, Instant startTime) {
        WeatherForecastCacheObject weatherForecast = inMemoryCache.getWeatherForecast(lat, lon, startTime);

        // AI USAGE :: Instead of writing this part (WeatherForecastEntry closest) myself, I paired with AI on it.

        WeatherForecastEntry closest = weatherForecast.entries().stream()
                .min(Comparator
                        .comparingLong((WeatherForecastEntry each) ->
                                Math.abs(Duration.between(each.forecastTime(), startTime).toSeconds()))
                        .thenComparing(WeatherForecastEntry::forecastTime, Comparator.reverseOrder()))
                .orElseThrow(() -> new IllegalStateException("No forecast entries available"));
        log.info("Found the closest forecast {} for lat {} and long {} and startTime {} ", closest, lat, lon, startTime);

        return new WeatherForecastResponse(closest.airTemperature(), closest.windSpeed());
    }


}

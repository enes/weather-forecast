package com.spond.weather.forecast.service.impl;

import com.spond.weather.forecast.model.WeatherForecastCacheObject;
import com.spond.weather.forecast.model.WeatherForecastEntry;
import com.spond.weather.forecast.model.WeatherForecastResponse;
import com.spond.weather.forecast.service.InMemoryCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class WeatherForecastServiceImplTest {

    @InjectMocks
    private WeatherForecastServiceImpl weatherForecastServiceImpl;
    @Mock
    private InMemoryCache inMemoryCache;

    private static final double LAT = 59.91;
    private static final double LON = 10.75;
    private static final Instant START_TIME = Instant.parse("2025-01-15T12:00:00Z");

    @Test
    public void shouldFindClosestEntry() {
        WeatherForecastEntry oneHourBefore = new WeatherForecastEntry(START_TIME.minus(1, ChronoUnit.HOURS), 5.0, 3.0);
        WeatherForecastEntry thirtyMinAfter = new WeatherForecastEntry(START_TIME.plus(1, ChronoUnit.MINUTES), 7.5, 4.2);
        WeatherForecastEntry threeHoursAfter = new WeatherForecastEntry(START_TIME.plus(1, ChronoUnit.HOURS), 10.0, 6.0);

        WeatherForecastCacheObject cached = new WeatherForecastCacheObject(
                List.of(oneHourBefore, thirtyMinAfter, threeHoursAfter),
                START_TIME.minus(1, ChronoUnit.HOURS),
                START_TIME.minus(1, ChronoUnit.HOURS),
                START_TIME.plus(2, ChronoUnit.HOURS)
        );
        when(inMemoryCache.getWeatherForecast(LAT, LON, START_TIME)).thenReturn(cached);

        WeatherForecastResponse response = weatherForecastServiceImpl.getWeatherForecast(LAT, LON, START_TIME);

        assertThat(response.airTemperature()).isEqualTo(thirtyMinAfter.airTemperature());
        assertThat(response.windSpeed()).isEqualTo(thirtyMinAfter.windSpeed());
    }


    @Test
    void shouldPickLaterEntryWhenTwoEntriesAreEquallyClose() {

        WeatherForecastEntry oneHourBefore = new WeatherForecastEntry(START_TIME.minus(1, ChronoUnit.HOURS), 5.0, 3.0);
        WeatherForecastEntry oneHourAfter = new WeatherForecastEntry(START_TIME.plus(1, ChronoUnit.HOURS), 9.0, 7.0);

        WeatherForecastCacheObject cached = new WeatherForecastCacheObject(
                List.of(oneHourBefore, oneHourAfter),
                START_TIME.minus(1, ChronoUnit.HOURS),
                START_TIME.minus(1, ChronoUnit.HOURS),
                START_TIME.plus(2, ChronoUnit.HOURS));

        when(inMemoryCache.getWeatherForecast(LAT, LON, START_TIME)).thenReturn(cached);

        WeatherForecastResponse response = weatherForecastServiceImpl.getWeatherForecast(LAT, LON, START_TIME);

        assertThat(response.airTemperature()).isEqualTo(oneHourAfter.airTemperature());
        assertThat(response.windSpeed()).isEqualTo(oneHourAfter.windSpeed());
    }
}
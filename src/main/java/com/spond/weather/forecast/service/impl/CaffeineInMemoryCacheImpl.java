package com.spond.weather.forecast.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.spond.weather.forecast.client.WeatherForecastClient;
import com.spond.weather.forecast.model.WeatherForecastCacheObject;
import com.spond.weather.forecast.model.WeatherForecastEntry;
import com.spond.weather.forecast.model.WeatherForecastResult;
import com.spond.weather.forecast.service.InMemoryCache;
import com.spond.weather.forecast.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaffeineInMemoryCacheImpl implements InMemoryCache {

    private final String CACHE_KEY = "lat=%s:lon=%s";
    private final Cache<String, WeatherForecastCacheObject> weatherForecastCache;
    private final WeatherForecastClient weatherForecastClient;

    @Override
    public WeatherForecastCacheObject getWeatherForecast(double lat, double lon, Instant startTime) {
        String cacheKey = String.format(CACHE_KEY, lat, lon);
        log.info("weather forecast cacheKey {}", cacheKey);

        WeatherForecastCacheObject cached = weatherForecastCache.getIfPresent(cacheKey);

        if (cached != null && isStillFresh(cached)) {
            log.info("cacheKey found in cache {}", cacheKey);
            return cached;
        }

        if (cached != null) {
            log.info("cacheKey {} found in cache but it is older than two hours validating it", cacheKey);
            weatherForecastCache.invalidate(cacheKey);
        }

        WeatherForecastCacheObject weatherForecastCacheObject = weatherForecastCache.get(cacheKey, key -> loadValue(lat, lon, cached == null ? null : cached.lastModifiedDate()));

        if (weatherForecastCacheObject != null) {
            return weatherForecastCacheObject;
        }

        return cached;

    }

    private boolean isStillFresh(WeatherForecastCacheObject cached) {
        if (TimeUtils.isOlderThanTwoHours(cached.lastUpdatedDate())) {
            return false;
        }
        if (cached.expires() != null) {
            return Instant.now().isBefore(cached.expires());
        }
        return true;
    }

    private WeatherForecastCacheObject loadValue(double lat, double lon, Instant lastModified) {
        log.info("Making request to metno api for lat {} and long {}", lat, lon);
        WeatherForecastResult weatherForecast = weatherForecastClient.getWeatherForecast(lat, lon, lastModified);

        if (weatherForecast == null) {
            return null;
        }
        Instant now = Instant.now();
        Instant sevenDaysFromNow = now.plus(7, ChronoUnit.DAYS);

        List<WeatherForecastEntry> nextSevenDaysForecast = weatherForecast.entries().stream()
                .filter(entry -> !entry.forecastTime().isBefore(now))
                .filter(entry -> entry.forecastTime().isBefore(sevenDaysFromNow))
                .sorted(Comparator.comparing(WeatherForecastEntry::forecastTime))
                .toList();

        if (nextSevenDaysForecast.isEmpty()) {
            throw new IllegalStateException("No forecast entries available for the next 7 days");
        }

        log.info("Found {} forecast entries for lat {} and long {} (next 7 days)",
                nextSevenDaysForecast.size(), lat, lon);


        return new WeatherForecastCacheObject(nextSevenDaysForecast, weatherForecast.updatedAt(), weatherForecast.lastModified(), weatherForecast.expires());
    }
}

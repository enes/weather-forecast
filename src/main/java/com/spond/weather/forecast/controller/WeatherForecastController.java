package com.spond.weather.forecast.controller;

import com.spond.weather.forecast.model.WeatherForecastResponse;
import com.spond.weather.forecast.service.WeatherForecastService;
import com.spond.weather.forecast.util.CoordinateUtils;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

import static com.spond.weather.forecast.util.TimeUtils.validateNotMoreThanSevenDaysInFuture;

@RestController
@RequestMapping("/api/v1/weather-forecast")
@RequiredArgsConstructor
@Slf4j
@Validated
public class WeatherForecastController {

    private final WeatherForecastService weatherForecastService;

    @GetMapping
    public ResponseEntity<WeatherForecastResponse> getWeatherForecast(@RequestParam
                                                                      @NotNull(message = "Latitude is required")
                                                                      @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
                                                                      @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
                                                                      Double lat,

                                                                      @RequestParam
                                                                      @NotNull(message = "Longitude is required")
                                                                      @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
                                                                      @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
                                                                      Double lon,

                                                                      @RequestParam
                                                                      @NotNull(message = "Start time is required")
                                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                      Instant startTime) {
        log.info("request is lat::{},lon::{}, startTime::{}", lat, lon, startTime);
        validateNotMoreThanSevenDaysInFuture(startTime);
        return ResponseEntity.ok(weatherForecastService.getWeatherForecast(CoordinateUtils.truncateToFourDigit(lat), CoordinateUtils.truncateToFourDigit(lon), startTime));
    }
}

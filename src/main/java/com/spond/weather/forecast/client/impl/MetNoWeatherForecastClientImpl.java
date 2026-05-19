package com.spond.weather.forecast.client.impl;

import com.spond.weather.forecast.client.WeatherForecastClient;
import com.spond.weather.forecast.exception.MetNoApiThrottledException;
import com.spond.weather.forecast.exception.MetNoClientErrorException;
import com.spond.weather.forecast.exception.MetNoUnavailableException;
import com.spond.weather.forecast.model.MetNoClientResponse;
import com.spond.weather.forecast.model.WeatherForecastEntry;
import com.spond.weather.forecast.model.WeatherForecastResult;
import com.spond.weather.forecast.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

import static com.spond.weather.forecast.util.TimeUtils.parseHttpDate;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;


@Service
@RequiredArgsConstructor
@Slf4j
public class MetNoWeatherForecastClientImpl implements WeatherForecastClient {

    private final RestClient metNoRestClient;

    private static final String API_PATH = "/weatherapi/locationforecast/2.0/compact";

    public WeatherForecastResult getWeatherForecast(double latitude, double longitude, Instant lastModified) {

        ResponseEntity<MetNoClientResponse> metNoClientResponse = metNoRestClient.get().uri(uriBuilder -> uriBuilder
                        .path(API_PATH)
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .headers(httpHeaders -> {
                    if (lastModified != null) {
                        httpHeaders.set(HttpHeaders.IF_MODIFIED_SINCE, TimeUtils.toHttpDate(lastModified));
                    }
                })
                .retrieve()
                .onStatus(status -> status.value() == TOO_MANY_REQUESTS.value(), (req, res) -> {
                    String retryAfter = res.getHeaders().getFirst(HttpHeaders.RETRY_AFTER);
                    log.error("MetNoApiThrottledException retryAfter {}", retryAfter);
                    throw new MetNoApiThrottledException(retryAfter);
                })

                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("MetNo ClientError Exception latitude:{}, longitude:{}", latitude, longitude);
                    throw new MetNoClientErrorException(
                            res.getStatusText());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("MetNo Unavailable Exception Exception latitude:{}, longitude:{}", latitude, longitude);
                    throw new MetNoUnavailableException(
                            "MetNoUnavailableException " + res.getStatusText());
                })

                .toEntity(MetNoClientResponse.class);

        if (metNoClientResponse.getStatusCode().value() == HttpStatus.NOT_MODIFIED.value()) {
            log.debug("Met.no returned 304 Not Modified for lat={}, lon={}", latitude, longitude);
            return null;
        }
        HttpHeaders headers = metNoClientResponse.getHeaders();

        Instant expires = parseHttpDate(headers.getFirst(HttpHeaders.EXPIRES));
        Instant lastModifiedDate = parseHttpDate(headers.getFirst(HttpHeaders.LAST_MODIFIED));
        WeatherForecastResult weatherForecastResult = toWeatherForecastResult(metNoClientResponse.getBody(), expires, lastModifiedDate);

        return weatherForecastResult;
    }

    private WeatherForecastResult toWeatherForecastResult(MetNoClientResponse response, Instant expires, Instant lastModified) {
        List<WeatherForecastEntry> weatherForecastEntries = response.properties()
                .timeseries()
                .stream()
                .map(each -> new WeatherForecastEntry(
                        each.time(),
                        each.data().instant().details().airTemperature(),
                        each.data().instant().details().windSpeed()
                ))
                .toList();

        return new WeatherForecastResult(
                response.properties().meta().updatedAt(), expires, lastModified,
                weatherForecastEntries
        );
    }
}

package com.spond.weather.forecast.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "metno.client")
public record MetNoClientProperties(
        String baseUrl,
        String userAgent,
        String headerType,
        Duration connectTimeout,
        Duration readTimeout
) {
}
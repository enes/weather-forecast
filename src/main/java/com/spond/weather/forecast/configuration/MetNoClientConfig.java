package com.spond.weather.forecast.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class MetNoClientConfig {

    private final MetNoClientProperties metNoClientProperties;

    @Bean
    public RestClient metNoRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(metNoClientProperties.baseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, metNoClientProperties.userAgent())
                .defaultHeader(HttpHeaders.ACCEPT, metNoClientProperties.headerType())
                .build();
    }
}

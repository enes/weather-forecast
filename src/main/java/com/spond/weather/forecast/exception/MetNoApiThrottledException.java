package com.spond.weather.forecast.exception;

public class MetNoApiThrottledException extends RuntimeException {
    public MetNoApiThrottledException(String message) {
        super(message);
    }
}

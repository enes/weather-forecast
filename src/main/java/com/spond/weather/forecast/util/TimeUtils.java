package com.spond.weather.forecast.util;

import com.spond.weather.forecast.exception.StartTimeException;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class TimeUtils {

    private static final Duration MAX_AGE = Duration.ofHours(2);

    public static boolean isOlderThanTwoHours(Instant lastUpdated) {
        Instant now = Instant.now();
        return lastUpdated.plus(MAX_AGE).isBefore(now);
    }

    public static void validateNotMoreThanSevenDaysInFuture(Instant requestedTime) {
        Objects.requireNonNull(requestedTime, "requestedTime must not be null");

        Instant eightDaysFromNow = Instant.now().plus(7, ChronoUnit.DAYS);
        if (requestedTime.isAfter(eightDaysFromNow)) {
            throw new StartTimeException(
                    "Requested time is more than 7 days in the future: " + requestedTime);
        }
    }

    public static String toHttpDate(Instant instant) {
        return DateTimeFormatter.RFC_1123_DATE_TIME
                .format(instant.atZone(ZoneOffset.UTC));
    }

    public static Instant parseHttpDate(String httpDate) {
        if (httpDate == null) {
            return null;
        }
        try {
            return ZonedDateTime.parse(httpDate, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}

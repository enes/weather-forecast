package com.spond.weather.forecast.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CoordinateUtils {

    public static double truncateToFourDigit(double coordinate) {
        return BigDecimal.valueOf(coordinate)
                .setScale(4, RoundingMode.DOWN)
                .doubleValue();
    }

}

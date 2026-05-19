package com.spond.weather.forecast.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordinateUtilsTest {

    @Test
    @DisplayName("Should not change a value that already has four decimals")
    void shouldNotChangeAlreadyFourDecimalValue() {
        double result = CoordinateUtils.truncateToFourDigit(10.5678);
        assertEquals(10.5678, result);
    }

    @Test
    @DisplayName("Should truncate positive coordinate to four decimal places")
    void shouldTruncatePositiveCoordinateToFourDecimals() {
        double result = CoordinateUtils.truncateToFourDigit(59.123456789);
        assertEquals(59.1234, result);
    }

    @Test
    @DisplayName("Should truncate negative coordinate to four decimal places")
    void shouldTruncateNegativeCoordinateToFourDecimals() {
        double result = CoordinateUtils.truncateToFourDigit(-59.123456789);
        assertEquals(-59.1234, result);
    }
}
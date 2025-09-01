package com.example.HNR.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for common LocalDate formatting and parsing operations.
 */
public final class DateUtils {

    private DateUtils() {
        // prevent instantiation
    }

    /**
     * Format the given {@link LocalDate} using the supplied pattern.
     *
     * @param date    the date to format, not null
     * @param pattern the pattern to apply, not null
     * @return the formatted date string
     * @throws IllegalArgumentException if date or pattern is null
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            throw new IllegalArgumentException("date and pattern must not be null");
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }


    public static LocalDate parse(String dateString, String pattern) {
        if (dateString == null || pattern == null) {
            throw new IllegalArgumentException("dateString and pattern must not be null");
        }
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }
}


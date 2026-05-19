package com.hms.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date and time formatting utilities.
 */
public final class DateFormatter {

    private DateFormatter() {}

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    /**
     * Parses a date string in yyyy-MM-dd format.
     * @return LocalDate or null if invalid
     */
    public static LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(text.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a time string in HH:mm format.
     * @return LocalTime or null if invalid
     */
    public static LocalTime parseTime(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        try {
            return LocalTime.parse(text.trim(), TIME_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Formats a LocalDate for display (e.g., 15-May-2026).
     */
    public static String formatForDisplay(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DISPLAY_DATE);
    }

    /**
     * Formats a LocalTime for display (e.g., 09:30).
     */
    public static String formatTime(LocalTime time) {
        if (time == null) return "N/A";
        return time.format(TIME_FORMAT);
    }
}

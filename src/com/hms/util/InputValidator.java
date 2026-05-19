package com.hms.util;

import java.time.LocalDate;
import com.hms.enums.Gender;
import com.hms.enums.BloodGroup;

/**
 * Validates user input from CLI menus.
 */
public final class InputValidator {

    private InputValidator() {}

    /**
     * Checks if string is non-null and non-empty.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Validates phone format (basic: at least 10 chars, starts with 0 or +).
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        phone = phone.trim().replaceAll("[\\s\\-]", "");
        return phone.matches("^[+0]\\d{9,14}$");
    }

    /**
     * Validates email format (basic regex).
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return true; // optional
        return email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$");
    }

    /**
     * Validates that a date is not in the future (for DOB).
     */
    public static boolean isValidDOB(LocalDate dob) {
        return dob != null && !dob.isAfter(LocalDate.now());
    }

    /**
     * Validates that a date is today or in the future (for appointments).
     */
    public static boolean isFutureOrToday(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    /**
     * Parses an integer from a string, returning -1 on failure.
     */
    public static int parseIntSafe(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Parses a double from a string, returning -1 on failure.
     */
    public static double parseDoubleSafe(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    /**
     * Validates gender string.
     */
    public static boolean isValidGender(String gender) {
        return Gender.fromString(gender) != null;
    }

    /**
     * Validates blood group display string.
     */
    public static boolean isValidBloodGroup(String bg) {
        try {
            BloodGroup.fromDisplay(bg);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

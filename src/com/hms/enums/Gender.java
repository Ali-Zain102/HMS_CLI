package com.hms.enums;

public enum Gender {
    MALE, FEMALE, OTHER;

    public static Gender fromString(String s) {
        return valueOf(s.trim().toUpperCase());
    }
}

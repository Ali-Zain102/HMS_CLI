package com.hms.enums;

public enum BloodGroup {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    private final String display;

    BloodGroup(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public static BloodGroup fromDisplay(String display) {
        for (BloodGroup bg : values()) {
            if (bg.display.equalsIgnoreCase(display)) return bg;
        }
        throw new IllegalArgumentException("Unknown blood group: " + display);
    }

    @Override
    public String toString() {
        return display;
    }
}

package com.hms.util;

/**
 * ANSI color codes for CLI output.
 * Usage: System.out.println(ConsoleColors.GREEN + "Success!" + ConsoleColors.RESET);
 */
public final class ConsoleColors {

    private ConsoleColors() {} // Utility class

    // Reset
    public static final String RESET  = "\033[0m";

    // Regular colors
    public static final String RED    = "\033[0;31m";
    public static final String GREEN  = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE   = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN   = "\033[0;36m";
    public static final String WHITE  = "\033[0;37m";

    // Bold
    public static final String BOLD       = "\033[1m";
    public static final String BOLD_RED   = "\033[1;31m";
    public static final String BOLD_GREEN = "\033[1;32m";
    public static final String BOLD_CYAN  = "\033[1;36m";
    public static final String BOLD_WHITE = "\033[1;37m";

    // Background
    public static final String BG_RED   = "\033[41m";
    public static final String BG_GREEN = "\033[42m";
    public static final String BG_BLUE  = "\033[44m";

    // ── Helper methods ────────────────────────────────────────

    public static String success(String msg) {
        return GREEN + msg + RESET;
    }

    public static String error(String msg) {
        return RED + msg + RESET;
    }

    public static String warn(String msg) {
        return YELLOW + msg + RESET;
    }

    public static String info(String msg) {
        return CYAN + msg + RESET;
    }

    public static String heading(String msg) {
        return BOLD_CYAN + msg + RESET;
    }
}

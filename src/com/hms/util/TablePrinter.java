package com.hms.util;

import java.util.List;

/**
 * Prints formatted tables to the console.
 * Demonstrates: Collections usage, String formatting.
 */
public final class TablePrinter {

    private TablePrinter() {}

    /**
     * Prints a formatted ASCII table from headers and rows.
     * @param headers column header names
     * @param rows    list of String arrays (each array = one row)
     */
    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return;

        int cols = headers.length;
        int[] widths = new int[cols];

        // Calculate column widths from headers
        for (int i = 0; i < cols; i++) {
            widths[i] = headers[i].length();
        }

        // Calculate column widths from data
        for (String[] row : rows) {
            for (int i = 0; i < cols && i < row.length; i++) {
                String cell = row[i] != null ? row[i] : "";
                widths[i] = Math.max(widths[i], cell.length());
            }
        }

        // Add padding
        for (int i = 0; i < cols; i++) {
            widths[i] += 2;
        }

        // Build format string
        StringBuilder fmt = new StringBuilder("|");
        for (int w : widths) {
            fmt.append(" %-").append(w).append("s|");
        }
        String format = fmt.toString();

        // Print separator
        String separator = buildSeparator(widths);

        System.out.println(separator);
        System.out.printf(format + "%n", (Object[]) headers);
        System.out.println(separator);

        if (rows.isEmpty()) {
            System.out.println("| " + centerText("No records found", totalWidth(widths) - 3) + "|");
        } else {
            for (String[] row : rows) {
                String[] padded = new String[cols];
                for (int i = 0; i < cols; i++) {
                    padded[i] = i < row.length && row[i] != null ? row[i] : "";
                }
                System.out.printf(format + "%n", (Object[]) padded);
            }
        }
        System.out.println(separator);
        System.out.println("Total: " + rows.size() + " record(s)");
    }

    /**
     * Builds a horizontal separator line.
     */
    private static String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) {
            sb.append(repeatChar('-', w + 2)).append("+");
        }
        return sb.toString();
    }

    /**
     * Total content width across all columns.
     */
    private static int totalWidth(int[] widths) {
        int total = 0;
        for (int w : widths) total += w + 3;
        return total;
    }

    /**
     * Centers text within a given width.
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int pad = (width - text.length()) / 2;
        return repeatChar(' ', pad) + text + repeatChar(' ', width - text.length() - pad);
    }

    /**
     * Repeats a character n times (Java 8 compatible).
     */
    private static String repeatChar(char c, int count) {
        if (count <= 0) return "";
        char[] arr = new char[count];
        java.util.Arrays.fill(arr, c);
        return new String(arr);
    }

    /**
     * Prints a simple key-value detail block.
     */
    public static void printDetail(String label, String value) {
        System.out.printf("  %-25s: %s%n", label, value != null ? value : "N/A");
    }
}

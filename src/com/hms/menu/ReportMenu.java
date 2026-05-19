package com.hms.menu;

import com.hms.database.DatabaseConnection;

import com.hms.util.ConsoleColors;
import com.hms.util.InputValidator;
import com.hms.util.TablePrinter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reports CLI Menu - uses DB views and functions for reports.
 * Demonstrates: Views, Cursors (via JDBC ResultSet iteration)
 */
public class ReportMenu {
    private final Scanner scanner;

    public ReportMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      📊 REPORTS                     ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. Patient Summary Report");
            System.out.println("  2. Doctor Schedule (Today)");
            System.out.println("  3. Bed Occupancy Report");
            System.out.println("  4. Pending Bills Report");
            System.out.println("  5. Low Stock Medicines");
            System.out.println("  6. Doctor List Report");
            System.out.println("  7. Audit Log");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);
            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: patientSummary(); break;
                    case 2: doctorSchedule(); break;
                    case 3: bedOccupancy(); break;
                    case 4: pendingBills(); break;
                    case 5: lowStock(); break;
                    case 6: doctorList(); break;
                    case 7: auditLog(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (Exception e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void patientSummary() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Patient Summary (from vw_PatientSummary) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM vw_PatientSummary";
        String[] headers = {"ID", "Name", "Gender", "Age", "Blood", "Phone", "Status", "Doctor", "Bed", "Ward", "Bill"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{
                    String.valueOf(rs.getInt("patient_id")), rs.getString("patient_name"),
                    rs.getString("gender"), String.valueOf(rs.getInt("age")),
                    rs.getString("blood_group"), rs.getString("phone"),
                    rs.getString("patient_status"),
                    rs.getString("assigned_doctor") != null ? rs.getString("assigned_doctor") : "-",
                    rs.getString("bed_number") != null ? rs.getString("bed_number") : "-",
                    rs.getString("ward") != null ? rs.getString("ward") : "-",
                    rs.getString("bill_status")
                });
            }
        }
        TablePrinter.printTable(headers, rows);
    }

    private void doctorSchedule() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Today's Schedule (from vw_DoctorScheduleToday) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM vw_DoctorScheduleToday";
        String[] headers = {"Appt ID", "Time", "Doctor", "Specialization", "Patient", "Phone", "Reason", "Status"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{
                    String.valueOf(rs.getInt("appointment_id")),
                    rs.getTime("appointment_time").toString(),
                    rs.getString("doctor_name"), rs.getString("specialization"),
                    rs.getString("patient_name"), rs.getString("patient_phone"),
                    rs.getString("reason"), rs.getString("status")
                });
            }
        }
        TablePrinter.printTable(headers, rows);
    }

    private void bedOccupancy() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Bed Occupancy (from vw_BedOccupancy) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM vw_BedOccupancy";
        String[] headers = {"Ward", "Total", "Available", "Occupied", "Maintenance", "Occupancy %"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{rs.getString("ward"),
                    String.valueOf(rs.getInt("total_beds")), String.valueOf(rs.getInt("available")),
                    String.valueOf(rs.getInt("occupied")), String.valueOf(rs.getInt("maintenance")),
                    rs.getString("occupancy_pct") + "%"});
            }
        }
        TablePrinter.printTable(headers, rows);
    }

    private void pendingBills() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Pending Bills (from vw_PendingBills) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM vw_PendingBills";
        String[] headers = {"Bill ID", "Patient", "Phone", "Date", "Total", "Paid", "Due", "Status"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{String.valueOf(rs.getInt("bill_id")),
                    rs.getString("patient_name"), rs.getString("patient_phone"),
                    rs.getDate("bill_date").toString(),
                    String.format("%.2f", rs.getDouble("total_amount")),
                    String.format("%.2f", rs.getDouble("amount_paid")),
                    String.format("%.2f", rs.getDouble("amount_due")),
                    rs.getString("status")});
            }
        }
        TablePrinter.printTable(headers, rows);
    }

    private void lowStock() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Low Stock (from vw_LowStockMedicines) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM vw_LowStockMedicines";
        String[] headers = {"ID", "Name", "Category", "Stock", "Reorder", "Price", "Expiry"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{String.valueOf(rs.getInt("medicine_id")),
                    rs.getString("name"), rs.getString("category"),
                    String.valueOf(rs.getInt("stock_quantity")),
                    String.valueOf(rs.getInt("reorder_level")),
                    String.format("%.2f", rs.getDouble("unit_price")),
                    rs.getDate("expiry_date") != null ? rs.getDate("expiry_date").toString() : "N/A"});
            }
        }
        TablePrinter.printTable(headers, rows);
    }

    private void doctorList() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Active Doctors (from vw_DoctorList) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM vw_DoctorList";
        String[] headers = {"ID", "Name", "Specialization", "Phone", "Email", "Department", "Appts Today"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new String[]{String.valueOf(rs.getInt("doctor_id")),
                    rs.getString("doctor_name"), rs.getString("specialization"),
                    rs.getString("phone"), rs.getString("email") != null ? rs.getString("email") : "-",
                    rs.getString("department") != null ? rs.getString("department") : "-",
                    String.valueOf(rs.getInt("appointments_today"))});
            }
        }
        TablePrinter.printTable(headers, rows);
    }

    private void auditLog() throws SQLException {
        System.out.println(ConsoleColors.heading("\n── Audit Log (last 20) ──"));
        Connection conn = DatabaseConnection.getInstance().getConnection();
        String sql = "SELECT * FROM audit_log ORDER BY changed_at DESC LIMIT 20";
        String[] headers = {"ID", "Table", "Record", "Action", "By", "At", "Old", "New"};
        List<String[]> rows = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String old = rs.getString("old_values");
                String nw = rs.getString("new_values");
                rows.add(new String[]{String.valueOf(rs.getInt("log_id")),
                    rs.getString("table_name"), String.valueOf(rs.getInt("record_id")),
                    rs.getString("action"), rs.getString("changed_by"),
                    rs.getTimestamp("changed_at").toString(),
                    old != null ? (old.length() > 30 ? old.substring(0,30) + "..." : old) : "-",
                    nw != null ? (nw.length() > 30 ? nw.substring(0,30) + "..." : nw) : "-"});
            }
        }
        TablePrinter.printTable(headers, rows);
    }
}

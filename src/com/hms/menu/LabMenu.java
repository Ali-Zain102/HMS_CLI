package com.hms.menu;

import com.hms.enums.LabStatus;
import com.hms.exception.HospitalException;
import com.hms.model.entity.LabReport;
import com.hms.service.ReportService;
import com.hms.util.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LabMenu {
    private final Scanner scanner;
    private final ReportService reportService;

    public LabMenu(Scanner scanner) {
        this.scanner = scanner;
        this.reportService = new ReportService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      🔬 LAB MANAGEMENT              ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. Request Lab Test");
            System.out.println("  2. View All Lab Reports");
            System.out.println("  3. View Pending Reports");
            System.out.println("  4. View Patient Reports");
            System.out.println("  5. Enter Lab Results");
            System.out.println("  6. View Report Details");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);
            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: requestTest(); break;
                    case 2: viewAll(); break;
                    case 3: viewPending(); break;
                    case 4: viewByPatient(); break;
                    case 5: enterResults(); break;
                    case 6: viewDetails(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void requestTest() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Request Lab Test ──"));
        System.out.print("  Patient ID: "); int pid = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Doctor ID: "); int did = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Test Name: "); String test = scanner.nextLine().trim();
        System.out.print("  Cost (PKR): "); double cost = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Notes (optional): "); String notes = scanner.nextLine().trim();

        if (pid <= 0 || did <= 0 || !InputValidator.isNotEmpty(test)) {
            System.out.println(ConsoleColors.error("  Invalid input!")); return;
        }
        LabReport lr = new LabReport();
        lr.setPatientId(pid); lr.setDoctorId(did); lr.setTestName(test);
        lr.setCost(Math.max(0, cost)); lr.setNotes(notes.isEmpty() ? null : notes);
        int id = reportService.requestLabTest(lr);
        System.out.println(ConsoleColors.success("  ✓ Lab test requested! ID: " + id));
    }

    private void viewAll() throws HospitalException { printTable(reportService.getAllReports()); }

    private void viewPending() throws HospitalException {
        List<LabReport> list = reportService.getPendingReports();
        if (list.isEmpty()) System.out.println(ConsoleColors.success("  No pending reports!"));
        else printTable(list);
    }

    private void viewByPatient() throws HospitalException {
        System.out.print("  Patient ID: ");
        int pid = InputValidator.parseIntSafe(scanner.nextLine());
        if (pid <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        List<LabReport> list = reportService.getPatientReports(pid);
        if (list.isEmpty()) System.out.println(ConsoleColors.warn("  No reports found."));
        else printTable(list);
    }

    private void enterResults() throws HospitalException {
        System.out.print("  Report ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        LabReport lr = reportService.getReportById(id);
        System.out.println("  Test: " + lr.getTestName() + " | Status: " + lr.getStatus());
        System.out.print("  Result: "); String result = scanner.nextLine().trim();
        System.out.print("  Notes: "); String notes = scanner.nextLine().trim();
        lr.setResult(result);
        lr.setResultDate(LocalDate.now());
        lr.setStatus(LabStatus.COMPLETED);
        if (!notes.isEmpty()) lr.setNotes(notes);
        reportService.enterResults(lr);
        System.out.println(ConsoleColors.success("  ✓ Results entered!"));
    }

    private void viewDetails() throws HospitalException {
        System.out.print("  Report ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        LabReport lr = reportService.getReportById(id);
        System.out.println(ConsoleColors.heading("\n  ═══ LAB REPORT ═══"));
        TablePrinter.printDetail("Report ID", String.valueOf(lr.getReportId()));
        TablePrinter.printDetail("Patient", lr.getPatientName());
        TablePrinter.printDetail("Test", lr.getTestName());
        TablePrinter.printDetail("Test Date", DateFormatter.formatForDisplay(lr.getTestDate()));
        TablePrinter.printDetail("Status", lr.getStatus().name());
        TablePrinter.printDetail("Result", lr.getResult() != null ? lr.getResult() : "Pending");
        TablePrinter.printDetail("Result Date", lr.getResultDate() != null ? lr.getResultDate().toString() : "N/A");
        TablePrinter.printDetail("Cost", String.format("PKR %.2f", lr.getCost()));
        TablePrinter.printDetail("Notes", lr.getNotes());
    }

    private void printTable(List<LabReport> reports) {
        String[] h = {"ID", "Patient", "Test", "Date", "Status", "Cost"};
        List<String[]> rows = new ArrayList<>();
        for (LabReport lr : reports) {
            rows.add(new String[]{String.valueOf(lr.getReportId()),
                lr.getPatientName() != null ? lr.getPatientName() : "ID:" + lr.getPatientId(),
                lr.getTestName(), DateFormatter.formatForDisplay(lr.getTestDate()),
                lr.getStatus().name(), String.format("%.2f", lr.getCost())});
        }
        TablePrinter.printTable(h, rows);
    }
}

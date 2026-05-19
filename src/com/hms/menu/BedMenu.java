package com.hms.menu;

import com.hms.enums.BedType;
import com.hms.exception.HospitalException;
import com.hms.model.entity.Bed;
import com.hms.service.BedService;
import com.hms.util.ConsoleColors;
import com.hms.util.InputValidator;
import com.hms.util.TablePrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Bed Management CLI Menu.
 */
public class BedMenu {

    private final Scanner scanner;
    private final BedService bedService;

    public BedMenu(Scanner scanner) {
        this.scanner = scanner;
        this.bedService = new BedService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      🛏️ BED MANAGEMENT              ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. View All Beds");
            System.out.println("  2. View Available Beds");
            System.out.println("  3. View Bed Details");
            System.out.println("  4. Add New Bed");
            System.out.println("  5. Bed Occupancy Report");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);

            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: viewAll(); break;
                    case 2: viewAvailable(); break;
                    case 3: viewDetails(); break;
                    case 4: addBed(); break;
                    case 5: occupancyReport(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void viewAll() throws HospitalException {
        List<Bed> beds = bedService.getAllBeds();
        printTable(beds);
    }

    private void viewAvailable() throws HospitalException {
        List<Bed> beds = bedService.getAvailableBeds();
        if (beds.isEmpty()) {
            System.out.println(ConsoleColors.warn("  No beds available!"));
        } else {
            printTable(beds);
        }
    }

    private void viewDetails() throws HospitalException {
        System.out.print("  Enter Bed ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Bed b = bedService.getBedById(id);
        System.out.println("\n  ── Bed Details ──");
        TablePrinter.printDetail("Bed ID", String.valueOf(b.getBedId()));
        TablePrinter.printDetail("Bed Number", b.getBedNumber());
        TablePrinter.printDetail("Ward", b.getWard());
        TablePrinter.printDetail("Type", b.getBedType().name());
        TablePrinter.printDetail("Status", b.getStatus().name());
        TablePrinter.printDetail("Daily Rate", "PKR " + String.format("%.2f", b.getDailyRate()));
        TablePrinter.printDetail("Patient ID", b.getPatientId() > 0 ? String.valueOf(b.getPatientId()) : "None");
    }

    private void addBed() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Add New Bed ──"));
        System.out.print("  Bed Number (e.g., G-104): ");
        String bedNum = scanner.nextLine().trim();
        System.out.print("  Ward: ");
        String ward = scanner.nextLine().trim();
        System.out.print("  Type (GENERAL/PRIVATE/ICU/EMERGENCY): ");
        String typeStr = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Daily Rate (PKR): ");
        double rate = InputValidator.parseDoubleSafe(scanner.nextLine());

        if (!InputValidator.isNotEmpty(bedNum) || rate <= 0) {
            System.out.println(ConsoleColors.error("  Invalid input!"));
            return;
        }

        BedType type;
        try { type = BedType.valueOf(typeStr); }
        catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.error("  Invalid bed type!"));
            return;
        }

        Bed bed = new Bed();
        bed.setBedNumber(bedNum);
        bed.setWard(ward);
        bed.setBedType(type);
        bed.setDailyRate(rate);

        int id = bedService.addBed(bed);
        System.out.println(ConsoleColors.success("  ✓ Bed added! ID: " + id));
    }

    private void occupancyReport() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Bed Occupancy Report ──"));
        String[] headers = {"Ward", "Total", "Available", "Occupied", "Maintenance", "Occupancy %"};
        List<String[]> rows = bedService.getOccupancySummary();
        TablePrinter.printTable(headers, rows);
    }

    private void printTable(List<Bed> beds) {
        String[] headers = {"ID", "Number", "Ward", "Type", "Status", "Rate/Day", "Patient"};
        List<String[]> rows = new ArrayList<>();
        for (Bed b : beds) {
            rows.add(new String[]{
                String.valueOf(b.getBedId()),
                b.getBedNumber(),
                b.getWard(),
                b.getBedType().name(),
                b.getStatus().name(),
                String.format("%.0f", b.getDailyRate()),
                b.getPatientId() > 0 ? String.valueOf(b.getPatientId()) : "-"
            });
        }
        TablePrinter.printTable(headers, rows);
    }
}

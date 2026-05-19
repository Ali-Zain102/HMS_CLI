package com.hms.menu;

import com.hms.exception.HospitalException;
import com.hms.model.entity.Medicine;
import com.hms.service.MedicineService;
import com.hms.util.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MedicineMenu {
    private final Scanner scanner;
    private final MedicineService medicineService;

    public MedicineMenu(Scanner scanner) {
        this.scanner = scanner;
        this.medicineService = new MedicineService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      💊 MEDICINE / PHARMACY         ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. View All Medicines");
            System.out.println("  2. Search Medicine by Name");
            System.out.println("  3. Add New Medicine");
            System.out.println("  4. Update Medicine");
            System.out.println("  5. Update Stock");
            System.out.println("  6. Low Stock Alerts");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);
            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: viewAll(); break;
                    case 2: search(); break;
                    case 3: addMedicine(); break;
                    case 4: updateMedicine(); break;
                    case 5: updateStock(); break;
                    case 6: lowStock(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void viewAll() throws HospitalException {
        printTable(medicineService.getAllMedicines());
    }

    private void search() throws HospitalException {
        System.out.print("  Medicine name: ");
        String name = scanner.nextLine().trim();
        List<Medicine> results = medicineService.searchMedicines(name);
        if (results.isEmpty()) System.out.println(ConsoleColors.warn("  Not found."));
        else printTable(results);
    }

    private void addMedicine() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Add Medicine ──"));
        System.out.print("  Name: "); String name = scanner.nextLine().trim();
        System.out.print("  Category: "); String cat = scanner.nextLine().trim();
        System.out.print("  Unit Price (PKR): "); double price = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Stock Quantity: "); int stock = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Reorder Level: "); int reorder = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Expiry Date (yyyy-MM-dd): "); LocalDate exp = DateFormatter.parseDate(scanner.nextLine());
        System.out.print("  Manufacturer: "); String mfr = scanner.nextLine().trim();

        if (!InputValidator.isNotEmpty(name) || price <= 0) {
            System.out.println(ConsoleColors.error("  Invalid input!")); return;
        }
        Medicine m = new Medicine();
        m.setName(name); m.setCategory(cat); m.setUnitPrice(price);
        m.setStockQuantity(stock); m.setReorderLevel(reorder);
        m.setExpiryDate(exp); m.setManufacturer(mfr);
        int id = medicineService.addMedicine(m);
        System.out.println(ConsoleColors.success("  ✓ Medicine added! ID: " + id));
    }

    private void updateMedicine() throws HospitalException {
        System.out.print("  Medicine ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        Medicine m = medicineService.getMedicineById(id);
        System.out.println("  Current: " + m.getName() + " | PKR " + m.getUnitPrice());
        System.out.print("  New Price (Enter to skip): "); String p = scanner.nextLine().trim();
        if (!p.isEmpty()) { double np = InputValidator.parseDoubleSafe(p); if (np > 0) m.setUnitPrice(np); }
        System.out.print("  New Category (Enter to skip): "); String c = scanner.nextLine().trim();
        if (!c.isEmpty()) m.setCategory(c);
        medicineService.updateMedicine(m);
        System.out.println(ConsoleColors.success("  ✓ Updated!"));
    }

    private void updateStock() throws HospitalException {
        System.out.print("  Medicine ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        Medicine m = medicineService.getMedicineById(id);
        System.out.println("  " + m.getName() + " | Current Stock: " + m.getStockQuantity());
        System.out.print("  New Stock Quantity: ");
        int qty = InputValidator.parseIntSafe(scanner.nextLine());
        if (qty < 0) { System.out.println(ConsoleColors.error("  Invalid quantity!")); return; }
        medicineService.updateStock(id, qty);
        System.out.println(ConsoleColors.success("  ✓ Stock updated!"));
    }

    private void lowStock() throws HospitalException {
        List<Medicine> low = medicineService.getLowStockMedicines();
        if (low.isEmpty()) System.out.println(ConsoleColors.success("  All stock levels OK!"));
        else {
            System.out.println(ConsoleColors.warn("\n  ⚠️  LOW STOCK ALERT ⚠️"));
            printTable(low);
        }
    }

    private void printTable(List<Medicine> meds) {
        String[] h = {"ID", "Name", "Category", "Price", "Stock", "Reorder", "Expiry"};
        List<String[]> rows = new ArrayList<>();
        for (Medicine m : meds) {
            rows.add(new String[]{String.valueOf(m.getMedicineId()), m.getName(),
                m.getCategory(), String.format("%.2f", m.getUnitPrice()),
                String.valueOf(m.getStockQuantity()), String.valueOf(m.getReorderLevel()),
                m.getExpiryDate() != null ? m.getExpiryDate().toString() : "N/A"});
        }
        TablePrinter.printTable(h, rows);
    }
}

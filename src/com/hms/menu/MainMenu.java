package com.hms.menu;

import com.hms.util.ConsoleColors;
import com.hms.util.InputValidator;

import java.util.Scanner;

/**
 * Main Menu - Entry point for all CLI modules.
 */
public class MainMenu {
    private final Scanner scanner;

    public MainMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║                                          ║"));
            System.out.println(ConsoleColors.heading("║    🏥  HOSPITAL MANAGEMENT SYSTEM  🏥    ║"));
            System.out.println(ConsoleColors.heading("║              HMS CLI v1.0                ║"));
            System.out.println(ConsoleColors.heading("║                                          ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════════╝"));
            System.out.println();
            System.out.println("  1. 👤 Patient Management");
            System.out.println("  2. 👨‍⚕️ Doctor Management");
            System.out.println("  3. 👩‍⚕️ Nurse Management");
            System.out.println("  4. 📅 Appointment Management");
            System.out.println("  5. 🛏️ Bed Management");
            System.out.println("  6. 💊 Medicine / Pharmacy");
            System.out.println("  7. 🔬 Lab Management");
            System.out.println("  8. 💰 Billing Management");
            System.out.println("  9. 📊 Reports");
            System.out.println("  0. 🚪 Exit");
            System.out.println();
            System.out.print(ConsoleColors.CYAN + "  Select Module: " + ConsoleColors.RESET);

            int choice = InputValidator.parseIntSafe(scanner.nextLine());

            switch (choice) {
                case 1: new PatientMenu(scanner).show(); break;
                case 2: new DoctorMenu(scanner).show(); break;
                case 3: new NurseMenu(scanner).show(); break;
                case 4: new AppointmentMenu(scanner).show(); break;
                case 5: new BedMenu(scanner).show(); break;
                case 6: new MedicineMenu(scanner).show(); break;
                case 7: new LabMenu(scanner).show(); break;
                case 8: new BillingMenu(scanner).show(); break;
                case 9: new ReportMenu(scanner).show(); break;
                case 0:
                    System.out.println(ConsoleColors.success("\n  Thank you for using HMS! Goodbye. 👋\n"));
                    return;
                default:
                    System.out.println(ConsoleColors.error("  Invalid choice! Please try again."));
            }
        }
    }
}

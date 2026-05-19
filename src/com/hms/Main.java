package com.hms;

import com.hms.database.DatabaseConnection;
import com.hms.menu.MainMenu;
import com.hms.util.ConsoleColors;

import java.util.Scanner;

/**
 * Hospital Management System - CLI Entry Point.
 *
 * Demonstrates:
 *  - Singleton Pattern     → DatabaseConnection
 *  - Factory Pattern       → Menu routing
 *  - DAO Pattern           → Data access layer
 *  - Encapsulation         → Private fields + Getters/Setters
 *  - Inheritance           → Person → Staff → Doctor → Surgeon
 *  - Polymorphism          → Method overriding & overloading
 *  - Abstraction           → Abstract classes & Interfaces
 *  - Composition           → Patient HAS-A MedicalRecord
 *  - Aggregation           → Doctor HAS-A Department
 *  - Generics              → GenericDAO<T, ID>
 *  - Custom Exceptions     → HospitalException hierarchy
 *  - Enums                 → BloodGroup, Gender, Status enums
 *  - Collections           → List, Map, Set usage throughout
 *  - Stored Procedures     → sp_RegisterPatient, sp_AdmitPatient, etc.
 *  - DB Functions          → fn_CalculateAge, fn_CalculateTotalBill
 *  - Triggers              → Audit logging, stock deduction
 *  - Views                 → vw_PatientSummary, vw_DoctorScheduleToday
 *  - Transactions          → START TRANSACTION, COMMIT, ROLLBACK
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(ConsoleColors.heading(
            "\n  ══════════════════════════════════════════════"));
        System.out.println(ConsoleColors.heading(
            "   🏥  Hospital Management System (HMS CLI)"));
        System.out.println(ConsoleColors.heading(
            "  ══════════════════════════════════════════════\n"));

        // Test database connection (Singleton)
        try {
            System.out.print(ConsoleColors.info("  Connecting to database... "));
            DatabaseConnection.getInstance();
            System.out.println(ConsoleColors.success("Connected! ✓"));
        } catch (Exception e) {
            System.out.println(ConsoleColors.error("FAILED! ✗"));
            System.out.println(ConsoleColors.error("  " + e.getMessage()));
            System.out.println(ConsoleColors.warn(
                "\n  Make sure XAMPP (MariaDB) is running and the database is set up."));
            System.out.println(ConsoleColors.warn(
                "  Run the SQL scripts in sql/ folder first.\n"));
            return;
        }

        // Launch main menu
        Scanner scanner = new Scanner(System.in);
        MainMenu mainMenu = new MainMenu(scanner);
        mainMenu.show();

        // Cleanup
        scanner.close();
        DatabaseConnection.getInstance().closeConnection();
    }
}

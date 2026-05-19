package com.hms.menu;

import com.hms.dao.NurseDAO;
import com.hms.enums.Gender;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Nurse;
import com.hms.util.ConsoleColors;
import com.hms.util.DateFormatter;
import com.hms.util.InputValidator;
import com.hms.util.TablePrinter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Nurse Management CLI Menu.
 */
public class NurseMenu {

    private final Scanner scanner;
    private final NurseDAO nurseDAO;

    public NurseMenu(Scanner scanner) {
        this.scanner = scanner;
        this.nurseDAO = new NurseDAO();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      👩‍⚕️ NURSE MANAGEMENT             ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. Add New Nurse");
            System.out.println("  2. View All Nurses");
            System.out.println("  3. Search Nurse by ID");
            System.out.println("  4. Search by Ward");
            System.out.println("  5. Update Nurse Info");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);

            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: addNurse(); break;
                    case 2: viewAll(); break;
                    case 3: searchById(); break;
                    case 4: searchByWard(); break;
                    case 5: updateNurse(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void addNurse() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Add New Nurse ──"));
        System.out.print("  First Name: ");
        String fn = scanner.nextLine().trim();
        System.out.print("  Last Name: ");
        String ln = scanner.nextLine().trim();
        System.out.print("  Gender (MALE/FEMALE/OTHER): ");
        Gender gender = Gender.fromString(scanner.nextLine());
        System.out.print("  Date of Birth (yyyy-MM-dd): ");
        LocalDate dob = DateFormatter.parseDate(scanner.nextLine());
        System.out.print("  Phone: ");
        String phone = scanner.nextLine().trim();
        System.out.print("  Email (optional): ");
        String email = scanner.nextLine().trim();
        System.out.print("  Department ID: ");
        int deptId = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Ward: ");
        String ward = scanner.nextLine().trim();
        System.out.print("  Salary (PKR): ");
        double salary = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Hire Date (yyyy-MM-dd): ");
        LocalDate hireDate = DateFormatter.parseDate(scanner.nextLine());

        if (!InputValidator.isNotEmpty(fn) || gender == null || dob == null || hireDate == null) {
            System.out.println(ConsoleColors.error("  Invalid input!"));
            return;
        }

        Nurse nurse = new Nurse();
        nurse.setFirstName(fn);
        nurse.setLastName(ln);
        nurse.setGender(gender);
        nurse.setDateOfBirth(dob);
        nurse.setPhone(phone);
        nurse.setEmail(email.isEmpty() ? null : email);
        nurse.setDepartmentId(deptId);
        nurse.setWard(ward);
        nurse.setSalary(salary);
        nurse.setHireDate(hireDate);

        int id = nurseDAO.insert(nurse);
        System.out.println(ConsoleColors.success("  ✓ Nurse added successfully! ID: " + id));
    }

    private void viewAll() throws HospitalException {
        List<Nurse> nurses = nurseDAO.findAll();
        printTable(nurses);
    }

    private void searchById() throws HospitalException {
        System.out.print("  Enter Nurse ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        Nurse n = nurseDAO.findById(id)
                .orElseThrow(() -> new HospitalException("Nurse not found with ID: " + id));
        n.displayInfo();
    }

    private void searchByWard() throws HospitalException {
        System.out.print("  Enter Ward name: ");
        String ward = scanner.nextLine().trim();
        List<Nurse> results = nurseDAO.findByWard(ward);
        if (results.isEmpty()) {
            System.out.println(ConsoleColors.warn("  No nurses found."));
        } else {
            printTable(results);
        }
    }

    private void updateNurse() throws HospitalException {
        System.out.print("  Enter Nurse ID to update: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Nurse n = nurseDAO.findById(id)
                .orElseThrow(() -> new HospitalException("Nurse not found with ID: " + id));
        System.out.println("  Current: " + n.getFullName() + " | Ward: " + n.getWard());

        System.out.print("  New Phone (Enter to skip): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) n.setPhone(phone);

        System.out.print("  New Ward (Enter to skip): ");
        String ward = scanner.nextLine().trim();
        if (!ward.isEmpty()) n.setWard(ward);

        System.out.print("  New Salary (Enter to skip): ");
        String salStr = scanner.nextLine().trim();
        if (!salStr.isEmpty()) {
            double sal = InputValidator.parseDoubleSafe(salStr);
            if (sal > 0) n.setSalary(sal);
        }

        nurseDAO.update(n);
        System.out.println(ConsoleColors.success("  ✓ Nurse updated successfully!"));
    }

    private void printTable(List<Nurse> nurses) {
        String[] headers = {"ID", "Name", "Gender", "Ward", "Phone", "Dept ID", "Active"};
        List<String[]> rows = new ArrayList<>();
        for (Nurse n : nurses) {
            rows.add(new String[]{
                String.valueOf(n.getId()),
                n.getFullName(),
                n.getGender() != null ? n.getGender().toString() : "?",
                n.getWard(),
                n.getPhone(),
                String.valueOf(n.getDepartmentId()),
                n.isActive() ? "Yes" : "No"
            });
        }
        TablePrinter.printTable(headers, rows);
    }
}

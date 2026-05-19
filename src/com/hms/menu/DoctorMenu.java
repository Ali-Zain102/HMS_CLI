package com.hms.menu;

import com.hms.enums.Gender;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Doctor;
import com.hms.service.DoctorService;
import com.hms.util.ConsoleColors;
import com.hms.util.DateFormatter;
import com.hms.util.InputValidator;
import com.hms.util.TablePrinter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Doctor Management CLI Menu.
 */
public class DoctorMenu {

    private final Scanner scanner;
    private final DoctorService doctorService;

    public DoctorMenu(Scanner scanner) {
        this.scanner = scanner;
        this.doctorService = new DoctorService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      👨‍⚕️ DOCTOR MANAGEMENT           ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. Add New Doctor");
            System.out.println("  2. View All Doctors");
            System.out.println("  3. Search Doctor by ID");
            System.out.println("  4. Search by Specialization");
            System.out.println("  5. Update Doctor Info");
            System.out.println("  6. View Active Doctors");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);

            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: addDoctor(); break;
                    case 2: viewAllDoctors(); break;
                    case 3: searchById(); break;
                    case 4: searchBySpec(); break;
                    case 5: updateDoctor(); break;
                    case 6: viewActive(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void addDoctor() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Add New Doctor ──"));

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
        System.out.print("  Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("  Specialization: ");
        String spec = scanner.nextLine().trim();
        System.out.print("  Department ID: ");
        int deptId = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Salary (PKR): ");
        double salary = InputValidator.parseDoubleSafe(scanner.nextLine());
        System.out.print("  Hire Date (yyyy-MM-dd): ");
        LocalDate hireDate = DateFormatter.parseDate(scanner.nextLine());

        if (!InputValidator.isNotEmpty(fn) || gender == null || dob == null || hireDate == null) {
            System.out.println(ConsoleColors.error("  Invalid input!"));
            return;
        }

        Doctor doctor = new Doctor();
        doctor.setFirstName(fn);
        doctor.setLastName(ln);
        doctor.setGender(gender);
        doctor.setDateOfBirth(dob);
        doctor.setPhone(phone);
        doctor.setEmail(email.isEmpty() ? null : email);
        doctor.setSpecialization(spec);
        doctor.setDepartmentId(deptId);
        doctor.setSalary(salary);
        doctor.setHireDate(hireDate);

        int id = doctorService.addDoctor(doctor);
        System.out.println(ConsoleColors.success("  ✓ Doctor added successfully! ID: " + id));
    }

    private void viewAllDoctors() throws HospitalException {
        List<Doctor> doctors = doctorService.getAllDoctors();
        printDoctorTable(doctors);
    }

    private void searchById() throws HospitalException {
        System.out.print("  Enter Doctor ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }
        Doctor d = doctorService.getDoctorById(id);
        d.displayInfo();
    }

    private void searchBySpec() throws HospitalException {
        System.out.print("  Enter Specialization: ");
        String spec = scanner.nextLine().trim();
        List<Doctor> results = doctorService.findBySpecialization(spec);
        if (results.isEmpty()) {
            System.out.println(ConsoleColors.warn("  No doctors found."));
        } else {
            printDoctorTable(results);
        }
    }

    private void updateDoctor() throws HospitalException {
        System.out.print("  Enter Doctor ID to update: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Doctor d = doctorService.getDoctorById(id);
        System.out.println("  Current: Dr. " + d.getFullName() + " | " + d.getSpecialization());

        System.out.print("  New Phone (Enter to skip): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) d.setPhone(phone);

        System.out.print("  New Email (Enter to skip): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) d.setEmail(email);

        System.out.print("  New Specialization (Enter to skip): ");
        String spec = scanner.nextLine().trim();
        if (!spec.isEmpty()) d.setSpecialization(spec);

        System.out.print("  New Salary (Enter to skip): ");
        String salStr = scanner.nextLine().trim();
        if (!salStr.isEmpty()) {
            double sal = InputValidator.parseDoubleSafe(salStr);
            if (sal > 0) d.setSalary(sal);
        }

        doctorService.updateDoctor(d);
        System.out.println(ConsoleColors.success("  ✓ Doctor updated successfully!"));
    }

    private void viewActive() throws HospitalException {
        List<Doctor> doctors = doctorService.getActiveDoctors();
        printDoctorTable(doctors);
    }

    private void printDoctorTable(List<Doctor> doctors) {
        String[] headers = {"ID", "Name", "Specialization", "Phone", "Dept ID", "Active"};
        List<String[]> rows = new ArrayList<>();
        for (Doctor d : doctors) {
            rows.add(new String[]{
                String.valueOf(d.getId()),
                "Dr. " + d.getFullName(),
                d.getSpecialization(),
                d.getPhone(),
                String.valueOf(d.getDepartmentId()),
                d.isActive() ? "Yes" : "No"
            });
        }
        TablePrinter.printTable(headers, rows);
    }
}

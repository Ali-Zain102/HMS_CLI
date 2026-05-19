package com.hms.menu;

import com.hms.enums.*;
import com.hms.exception.HospitalException;
import com.hms.model.concrete.Patient;
import com.hms.service.PatientService;
import com.hms.util.ConsoleColors;
import com.hms.util.DateFormatter;
import com.hms.util.InputValidator;
import com.hms.util.TablePrinter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Patient Management CLI Menu.
 */
public class PatientMenu {

    private final Scanner scanner;
    private final PatientService patientService;

    public PatientMenu(Scanner scanner) {
        this.scanner = scanner;
        this.patientService = new PatientService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      👤 PATIENT MANAGEMENT          ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. Register New Patient");
            System.out.println("  2. View All Patients");
            System.out.println("  3. Search Patient by ID");
            System.out.println("  4. Search Patient by Name");
            System.out.println("  5. Update Patient Info");
            System.out.println("  6. Admit Patient");
            System.out.println("  7. Discharge Patient");
            System.out.println("  8. View Patients by Status");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);

            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: registerPatient(); break;
                    case 2: viewAllPatients(); break;
                    case 3: searchById(); break;
                    case 4: searchByName(); break;
                    case 5: updatePatient(); break;
                    case 6: admitPatient(); break;
                    case 7: dischargePatient(); break;
                    case 8: viewByStatus(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void registerPatient() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Register New Patient ──"));

        System.out.print("  First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("  Last Name: ");
        String lastName = scanner.nextLine().trim();

        if (!InputValidator.isNotEmpty(firstName) || !InputValidator.isNotEmpty(lastName)) {
            System.out.println(ConsoleColors.error("  Name cannot be empty!"));
            return;
        }

        System.out.print("  Gender (MALE/FEMALE/OTHER): ");
        Gender gender = Gender.fromString(scanner.nextLine());
        if (gender == null) {
            System.out.println(ConsoleColors.error("  Invalid gender!"));
            return;
        }

        System.out.print("  Date of Birth (yyyy-MM-dd): ");
        LocalDate dob = DateFormatter.parseDate(scanner.nextLine());
        if (!InputValidator.isValidDOB(dob)) {
            System.out.println(ConsoleColors.error("  Invalid date of birth!"));
            return;
        }

        System.out.print("  Blood Group (A+, A-, B+, B-, AB+, AB-, O+, O-): ");
        String bgStr = scanner.nextLine().trim();
        BloodGroup bloodGroup = null;
        if (!bgStr.isEmpty()) {
            try { bloodGroup = BloodGroup.fromDisplay(bgStr); }
            catch (IllegalArgumentException e) {
                System.out.println(ConsoleColors.warn("  Unknown blood group, setting as null."));
            }
        }

        System.out.print("  Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.print("  Email (optional): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = null;

        System.out.print("  Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("  Emergency Contact Name: ");
        String ecName = scanner.nextLine().trim();

        System.out.print("  Emergency Contact Phone: ");
        String ecPhone = scanner.nextLine().trim();

        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setGender(gender);
        patient.setDateOfBirth(dob);
        patient.setBloodGroup(bloodGroup);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setEmergencyContactName(ecName);
        patient.setEmergencyContactPhone(ecPhone);

        // Use Address object for composition demo
        if (!address.isEmpty()) {
            com.hms.model.composition.Address addr = new com.hms.model.composition.Address();
            addr.setStreet(address);
            patient.setAddress(addr);
        }

        int id = patientService.registerPatient(patient);
        System.out.println(ConsoleColors.success("  ✓ Patient registered successfully! ID: " + id));
    }

    private void viewAllPatients() throws HospitalException {
        List<Patient> patients = patientService.getAllPatients();
        printPatientTable(patients);
    }

    private void searchById() throws HospitalException {
        System.out.print("  Enter Patient ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Patient p = patientService.getPatientById(id);
        p.displayInfo();
    }

    private void searchByName() throws HospitalException {
        System.out.print("  Enter name to search: ");
        String name = scanner.nextLine().trim();
        List<Patient> results = patientService.searchPatients(name);
        if (results.isEmpty()) {
            System.out.println(ConsoleColors.warn("  No patients found."));
        } else {
            printPatientTable(results);
        }
    }

    private void updatePatient() throws HospitalException {
        System.out.print("  Enter Patient ID to update: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Patient p = patientService.getPatientById(id);
        System.out.println("  Current: " + p.getFullName() + " | " + p.getPhone());

        System.out.print("  New Phone (press Enter to skip): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) p.setPhone(phone);

        System.out.print("  New Email (press Enter to skip): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) p.setEmail(email);

        System.out.print("  New Address (press Enter to skip): ");
        String addr = scanner.nextLine().trim();
        if (!addr.isEmpty()) {
            com.hms.model.composition.Address address = new com.hms.model.composition.Address();
            address.setStreet(addr);
            p.setAddress(address);
        }

        patientService.updatePatient(p);
        System.out.println(ConsoleColors.success("  ✓ Patient updated successfully!"));
    }

    private void admitPatient() throws HospitalException {
        System.out.print("  Patient ID: ");
        int patientId = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Bed ID: ");
        int bedId = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Doctor ID: ");
        int doctorId = InputValidator.parseIntSafe(scanner.nextLine());

        if (patientId <= 0 || bedId <= 0 || doctorId <= 0) {
            System.out.println(ConsoleColors.error("  Invalid input!"));
            return;
        }

        String message = patientService.admitPatient(patientId, bedId, doctorId);
        System.out.println(ConsoleColors.success("  ✓ " + message));
    }

    private void dischargePatient() throws HospitalException {
        System.out.print("  Patient ID: ");
        int patientId = InputValidator.parseIntSafe(scanner.nextLine());
        if (patientId <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        System.out.print("  Discount (PKR, 0 for none): ");
        double discount = InputValidator.parseDoubleSafe(scanner.nextLine());
        if (discount < 0) discount = 0;

        String message = patientService.dischargePatient(patientId, discount);
        System.out.println(ConsoleColors.success("  ✓ " + message));
    }

    private void viewByStatus() throws HospitalException {
        System.out.print("  Status (REGISTERED/ADMITTED/DISCHARGED): ");
        String statusStr = scanner.nextLine().trim().toUpperCase();
        try {
            PatientStatus status = PatientStatus.valueOf(statusStr);
            List<Patient> patients = patientService.getPatientsByStatus(status);
            printPatientTable(patients);
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.error("  Invalid status!"));
        }
    }

    private void printPatientTable(List<Patient> patients) {
        String[] headers = {"ID", "Name", "Gender", "Age", "Blood", "Phone", "Status"};
        List<String[]> rows = new ArrayList<>();
        for (Patient p : patients) {
            rows.add(new String[]{
                String.valueOf(p.getId()),
                p.getFullName(),
                p.getGender() != null ? p.getGender().toString() : "?",
                String.valueOf(p.getAge()),
                p.getBloodGroup() != null ? p.getBloodGroup().toString() : "?",
                p.getPhone(),
                p.getStatus() != null ? p.getStatus().name() : "?"
            });
        }
        TablePrinter.printTable(headers, rows);
    }
}

package com.hms.menu;

import com.hms.exception.HospitalException;
import com.hms.model.entity.Appointment;
import com.hms.service.AppointmentService;
import com.hms.util.ConsoleColors;
import com.hms.util.DateFormatter;
import com.hms.util.InputValidator;
import com.hms.util.TablePrinter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Appointment Management CLI Menu.
 */
public class AppointmentMenu {

    private final Scanner scanner;
    private final AppointmentService appointmentService;

    public AppointmentMenu(Scanner scanner) {
        this.scanner = scanner;
        this.appointmentService = new AppointmentService();
    }

    public void show() {
        while (true) {
            System.out.println(ConsoleColors.heading("\n╔══════════════════════════════════════╗"));
            System.out.println(ConsoleColors.heading("║      📅 APPOINTMENT MANAGEMENT      ║"));
            System.out.println(ConsoleColors.heading("╚══════════════════════════════════════╝"));
            System.out.println("  1. Book New Appointment");
            System.out.println("  2. View All Appointments");
            System.out.println("  3. View Today's Appointments");
            System.out.println("  4. Search by Appointment ID");
            System.out.println("  5. View Patient's Appointments");
            System.out.println("  6. Cancel Appointment");
            System.out.println("  7. Update Appointment Status");
            System.out.println("  0. ← Back to Main Menu");
            System.out.print(ConsoleColors.CYAN + "  Choose: " + ConsoleColors.RESET);

            int choice = InputValidator.parseIntSafe(scanner.nextLine());
            try {
                switch (choice) {
                    case 1: bookAppointment(); break;
                    case 2: viewAll(); break;
                    case 3: viewToday(); break;
                    case 4: searchById(); break;
                    case 5: viewByPatient(); break;
                    case 6: cancelAppointment(); break;
                    case 7: updateStatus(); break;
                    case 0: return;
                    default: System.out.println(ConsoleColors.error("  Invalid choice!"));
                }
            } catch (HospitalException e) {
                System.out.println(ConsoleColors.error("  Error: " + e.getMessage()));
            }
        }
    }

    private void bookAppointment() throws HospitalException {
        System.out.println(ConsoleColors.heading("\n── Book New Appointment ──"));
        System.out.print("  Patient ID: ");
        int patientId = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Doctor ID: ");
        int doctorId = InputValidator.parseIntSafe(scanner.nextLine());
        System.out.print("  Date (yyyy-MM-dd): ");
        LocalDate date = DateFormatter.parseDate(scanner.nextLine());
        System.out.print("  Time (HH:mm): ");
        LocalTime time = DateFormatter.parseTime(scanner.nextLine());
        System.out.print("  Reason: ");
        String reason = scanner.nextLine().trim();

        if (patientId <= 0 || doctorId <= 0 || date == null || time == null) {
            System.out.println(ConsoleColors.error("  Invalid input!"));
            return;
        }

        Appointment appt = new Appointment();
        appt.setPatientId(patientId);
        appt.setDoctorId(doctorId);
        appt.setAppointmentDate(date);
        appt.setAppointmentTime(time);
        appt.setReason(reason);

        int id = appointmentService.bookAppointment(appt);
        System.out.println(ConsoleColors.success("  ✓ Appointment booked! ID: " + id));
    }

    private void viewAll() throws HospitalException {
        List<Appointment> list = appointmentService.getAllAppointments();
        printTable(list);
    }

    private void viewToday() throws HospitalException {
        List<Appointment> list = appointmentService.getTodayAppointments();
        if (list.isEmpty()) {
            System.out.println(ConsoleColors.warn("  No appointments scheduled for today."));
        } else {
            printTable(list);
        }
    }

    private void searchById() throws HospitalException {
        System.out.print("  Enter Appointment ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Appointment a = appointmentService.getAppointmentById(id);
        System.out.println("\n  ── Appointment Details ──");
        TablePrinter.printDetail("Appointment ID", String.valueOf(a.getAppointmentId()));
        TablePrinter.printDetail("Patient", a.getPatientName());
        TablePrinter.printDetail("Doctor", a.getDoctorName());
        TablePrinter.printDetail("Date", DateFormatter.formatForDisplay(a.getAppointmentDate()));
        TablePrinter.printDetail("Time", DateFormatter.formatTime(a.getAppointmentTime()));
        TablePrinter.printDetail("Reason", a.getReason());
        TablePrinter.printDetail("Status", a.getStatus().name());
        TablePrinter.printDetail("Notes", a.getNotes());
    }

    private void viewByPatient() throws HospitalException {
        System.out.print("  Enter Patient ID: ");
        int patientId = InputValidator.parseIntSafe(scanner.nextLine());
        if (patientId <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        List<Appointment> list = appointmentService.getPatientAppointments(patientId);
        if (list.isEmpty()) {
            System.out.println(ConsoleColors.warn("  No appointments found for this patient."));
        } else {
            printTable(list);
        }
    }

    private void cancelAppointment() throws HospitalException {
        System.out.print("  Enter Appointment ID to cancel: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        appointmentService.cancelAppointment(id);
        System.out.println(ConsoleColors.success("  ✓ Appointment cancelled!"));
    }

    private void updateStatus() throws HospitalException {
        System.out.print("  Enter Appointment ID: ");
        int id = InputValidator.parseIntSafe(scanner.nextLine());
        if (id <= 0) { System.out.println(ConsoleColors.error("  Invalid ID!")); return; }

        Appointment a = appointmentService.getAppointmentById(id);
        System.out.println("  Current Status: " + a.getStatus());
        System.out.print("  New Status (SCHEDULED/COMPLETED/CANCELLED/NO_SHOW): ");
        String statusStr = scanner.nextLine().trim().toUpperCase();

        try {
            a.setStatus(com.hms.enums.AppointmentStatus.valueOf(statusStr));
            System.out.print("  Notes (optional): ");
            String notes = scanner.nextLine().trim();
            if (!notes.isEmpty()) a.setNotes(notes);
            appointmentService.updateAppointment(a);
            System.out.println(ConsoleColors.success("  ✓ Status updated!"));
        } catch (IllegalArgumentException e) {
            System.out.println(ConsoleColors.error("  Invalid status!"));
        }
    }

    private void printTable(List<Appointment> list) {
        String[] headers = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"};
        List<String[]> rows = new ArrayList<>();
        for (Appointment a : list) {
            rows.add(new String[]{
                String.valueOf(a.getAppointmentId()),
                a.getPatientName(),
                a.getDoctorName(),
                DateFormatter.formatForDisplay(a.getAppointmentDate()),
                DateFormatter.formatTime(a.getAppointmentTime()),
                a.getReason() != null ? a.getReason() : "",
                a.getStatus().name()
            });
        }
        TablePrinter.printTable(headers, rows);
    }
}

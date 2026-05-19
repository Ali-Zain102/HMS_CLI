package com.hms.model.concrete;

import com.hms.enums.Gender;
import com.hms.interfaces.Schedulable;
import com.hms.model.abstracts.Staff;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor: extends Staff, implements Schedulable.
 * Demonstrates: Inheritance, Interface implementation, Polymorphism
 */
public class Doctor extends Staff implements Schedulable {

    private String specialization;

    // ── Constructors ─────────────────────────────────────────

    public Doctor() {
        super();
    }

    public Doctor(int id, String firstName, String lastName,
                  Gender gender, LocalDate dateOfBirth,
                  String phone, String email,
                  String specialization,
                  int departmentId, double salary, LocalDate hireDate) {
        super(id, firstName, lastName, gender, dateOfBirth,
              phone, email, departmentId, salary, hireDate);
        this.specialization = specialization;
    }

    // ── Getters & Setters ────────────────────────────────────

    public String getSpecialization()          { return specialization; }
    public void setSpecialization(String s)    { this.specialization = s; }

    // ── Abstract method implementations ──────────────────────

    @Override
    public String getRole() {
        return "Doctor";
    }

    @Override
    public String getDuties() {
        return "Diagnose patients, prescribe treatment, perform procedures.";
    }

    // ── Schedulable interface ─────────────────────────────────

    @Override
    public List<String> getSchedule(LocalDate date) {
        // Actual schedule loaded from DB in DoctorService
        // This returns a placeholder — real data via DAO
        List<String> schedule = new ArrayList<>();
        schedule.add("Schedule for " + getFullName() + " on " + date + " (load from DB)");
        return schedule;
    }

    @Override
    public boolean isAvailable(LocalDate date, LocalTime time) {
        // Actual check done in AppointmentService via DB
        return true;
    }

    // ── Polymorphism: override displayInfo ────────────────────

    @Override
    public void displayInfo() {
        System.out.println("=== Doctor Details ===");
        super.displayInfo();
        System.out.println("Specialization: " + specialization);
    }

    @Override
    public String toString() {
        return String.format("Dr. %s | %s | Dept ID: %d | Phone: %s",
                getFullName(), specialization, getDepartmentId(), getPhone());
    }
}

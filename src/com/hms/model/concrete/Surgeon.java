package com.hms.model.concrete;

import com.hms.enums.Gender;
import java.time.LocalDate;

/**
 * Surgeon extends Doctor.
 * Demonstrates: Multi-level Inheritance (Person → Staff → Doctor → Surgeon)
 */
public class Surgeon extends Doctor {

    private String surgeryType;       // e.g. "Cardiac", "Neuro", "Ortho"
    private int    surgeriesPerformed;

    public Surgeon() {
        super();
    }

    public Surgeon(int id, String firstName, String lastName,
                   Gender gender, LocalDate dateOfBirth,
                   String phone, String email,
                   String specialization, String surgeryType,
                   int departmentId, double salary, LocalDate hireDate) {
        super(id, firstName, lastName, gender, dateOfBirth, phone, email,
              specialization, departmentId, salary, hireDate);
        this.surgeryType        = surgeryType;
        this.surgeriesPerformed = 0;
    }

    // ── Getters & Setters ─────────────────────────────────────

    public String getSurgeryType()             { return surgeryType; }
    public void setSurgeryType(String s)       { this.surgeryType = s; }

    public int getSurgeriesPerformed()         { return surgeriesPerformed; }
    public void setSurgeriesPerformed(int n)   { this.surgeriesPerformed = n; }

    public void recordSurgery()                { this.surgeriesPerformed++; }

    // ── Overrides ─────────────────────────────────────────────

    @Override
    public String getRole() {
        return "Surgeon";
    }

    @Override
    public String getDuties() {
        return "Perform " + surgeryType + " surgeries and post-op care.";
    }

    /**
     * Polymorphism: method overloading (different parameter)
     */
    public void displayInfo(boolean showSurgeryStats) {
        displayInfo();
        if (showSurgeryStats) {
            System.out.println("Surgery Type      : " + surgeryType);
            System.out.println("Surgeries Done    : " + surgeriesPerformed);
        }
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Surgeon Details ===");
        super.displayInfo();
        System.out.println("Surgery Type  : " + surgeryType);
    }

    @Override
    public String toString() {
        return String.format("Surgeon %s | %s | %s | Phone: %s",
                getFullName(), getSpecialization(), surgeryType, getPhone());
    }
}

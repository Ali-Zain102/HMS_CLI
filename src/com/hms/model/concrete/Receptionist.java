package com.hms.model.concrete;

import com.hms.enums.Gender;
import com.hms.model.abstracts.Staff;

import java.time.LocalDate;

/**
 * Receptionist extends Staff.
 * Demonstrates: Inheritance
 */
public class Receptionist extends Staff {

    private String desk;

    public Receptionist() {
        super();
    }

    public Receptionist(int id, String firstName, String lastName,
                        Gender gender, LocalDate dateOfBirth,
                        String phone, String email,
                        int departmentId, String desk,
                        double salary, LocalDate hireDate) {
        super(id, firstName, lastName, gender, dateOfBirth,
              phone, email, departmentId, salary, hireDate);
        this.desk = desk;
    }

    public String getDesk()          { return desk; }
    public void setDesk(String desk) { this.desk = desk; }

    @Override
    public String getRole() { return "Receptionist"; }

    @Override
    public String getDuties() {
        return "Manage patient registrations, appointments, and front desk at " + desk + ".";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Receptionist Details ===");
        super.displayInfo();
        System.out.println("Desk      : " + desk);
    }
}

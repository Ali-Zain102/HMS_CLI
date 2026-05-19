package com.hms.model.concrete;

import com.hms.enums.Gender;
import com.hms.model.abstracts.Staff;

import java.time.LocalDate;

/**
 * Nurse extends Staff.
 * Demonstrates: Inheritance (Person → Staff → Nurse)
 */
public class Nurse extends Staff {

    private String ward;

    public Nurse() {
        super();
    }

    public Nurse(int id, String firstName, String lastName,
                 Gender gender, LocalDate dateOfBirth,
                 String phone, String email,
                 int departmentId, String ward,
                 double salary, LocalDate hireDate) {
        super(id, firstName, lastName, gender, dateOfBirth,
              phone, email, departmentId, salary, hireDate);
        this.ward = ward;
    }

    public String getWard()             { return ward; }
    public void setWard(String ward)    { this.ward = ward; }

    @Override
    public String getRole() {
        return "Nurse";
    }

    @Override
    public String getDuties() {
        return "Provide patient care in " + ward + " ward.";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Nurse Details ===");
        super.displayInfo();
        System.out.println("Ward      : " + ward);
    }

    @Override
    public String toString() {
        return String.format("Nurse %s | Ward: %s | Phone: %s",
                getFullName(), ward, getPhone());
    }
}

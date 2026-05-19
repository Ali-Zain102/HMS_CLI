package com.hms.model.abstracts;

import com.hms.enums.Gender;
import java.time.LocalDate;

/**
 * Abstract Staff class extends Person.
 * Demonstrates: Inheritance (Person → Staff)
 */
public abstract class Staff extends Person {

    private int       departmentId;
    private double    salary;
    private LocalDate hireDate;
    private boolean   active;

    protected Staff(int id, String firstName, String lastName,
                    Gender gender, LocalDate dateOfBirth,
                    String phone, String email,
                    int departmentId, double salary,
                    LocalDate hireDate) {
        super(id, firstName, lastName, gender, dateOfBirth, phone, email);
        this.departmentId = departmentId;
        this.salary       = salary;
        this.hireDate     = hireDate;
        this.active       = true;
    }

    protected Staff() {
        super();
    }

    // ── Getters & Setters ────────────────────────────────────

    public int getDepartmentId()           { return departmentId; }
    public void setDepartmentId(int d)     { this.departmentId = d; }

    public double getSalary()              { return salary; }
    public void setSalary(double s)        { this.salary = s; }

    public LocalDate getHireDate()         { return hireDate; }
    public void setHireDate(LocalDate d)   { this.hireDate = d; }

    public boolean isActive()              { return active; }
    public void setActive(boolean a)       { this.active = a; }

    /**
     * Returns years of service since hire date.
     */
    public int getYearsOfService() {
        if (hireDate == null) return 0;
        return LocalDate.now().getYear() - hireDate.getYear();
    }

    /**
     * Polymorphism: extends displayInfo from Person
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Salary    : PKR " + String.format("%.2f", salary));
        System.out.println("Hired     : " + hireDate);
        System.out.println("Experience: " + getYearsOfService() + " year(s)");
        System.out.println("Active    : " + (active ? "Yes" : "No"));
    }

    /**
     * Abstract: each staff type defines its specific duties.
     * Demonstrates: Abstraction
     */
    public abstract String getDuties();
}

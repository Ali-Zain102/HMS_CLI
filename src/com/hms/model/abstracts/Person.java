package com.hms.model.abstracts;

import com.hms.enums.Gender;
import java.time.LocalDate;
import java.time.Period;

/**
 * Abstract base class for all persons in the system.
 * Demonstrates: Abstraction, Encapsulation
 */
public abstract class Person {

    // Encapsulation: private fields
    private int    id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;

    // Constructor
    protected Person(int id, String firstName, String lastName,
                     Gender gender, LocalDate dateOfBirth,
                     String phone, String email) {
        this.id          = id;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.gender      = gender;
        this.dateOfBirth = dateOfBirth;
        this.phone       = phone;
        this.email       = email;
    }

    // No-arg constructor for DAO construction
    protected Person() {}

    // ── Getters & Setters ────────────────────────────────────

    public int getId()                     { return id; }
    public void setId(int id)              { this.id = id; }

    public String getFirstName()           { return firstName; }
    public void setFirstName(String fn)    { this.firstName = fn; }

    public String getLastName()            { return lastName; }
    public void setLastName(String ln)     { this.lastName = ln; }

    public Gender getGender()              { return gender; }
    public void setGender(Gender g)        { this.gender = g; }

    public LocalDate getDateOfBirth()      { return dateOfBirth; }
    public void setDateOfBirth(LocalDate d){ this.dateOfBirth = d; }

    public String getPhone()               { return phone; }
    public void setPhone(String p)         { this.phone = p; }

    public String getEmail()               { return email; }
    public void setEmail(String e)         { this.email = e; }

    // ── Utility ─────────────────────────────────────────────

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    // ── Abstract method: subclasses must implement ───────────

    /**
     * Returns a one-line role-specific summary.
     * Demonstrates: Abstraction (abstract method)
     */
    public abstract String getRole();

    /**
     * Displays person details. Overridden in subclasses.
     * Demonstrates: Polymorphism (method overriding)
     */
    public void displayInfo() {
        System.out.println("ID        : " + id);
        System.out.println("Name      : " + getFullName());
        System.out.println("Gender    : " + gender);
        System.out.println("Age       : " + getAge());
        System.out.println("Phone     : " + phone);
        System.out.println("Email     : " + (email != null ? email : "N/A"));
        System.out.println("Role      : " + getRole());
    }

    @Override
    public String toString() {
        return String.format("[%s] ID=%d | %s | Age=%d | Phone=%s",
                getRole(), id, getFullName(), getAge(), phone);
    }
}

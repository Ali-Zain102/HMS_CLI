package com.hms.model.concrete;

import com.hms.enums.BloodGroup;
import com.hms.enums.Gender;
import com.hms.enums.PatientStatus;
import com.hms.model.abstracts.Person;
import com.hms.model.composition.Address;
import com.hms.model.composition.MedicalRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient extends Person.
 * Demonstrates:
 *   - Inheritance (Person → Patient)
 *   - Composition: Patient HAS-A Address, HAS MedicalRecords (dies together)
 */
public class Patient extends Person {

    private BloodGroup    bloodGroup;
    private PatientStatus status;
    private int           assignedDoctorId;

    // Emergency contact info
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Composition: Address belongs to Patient (dies with Patient)
    private Address address;

    // Composition: list of medical records owned by Patient
    private List<MedicalRecord> medicalHistory;

    // ── Constructors ─────────────────────────────────────────

    public Patient() {
        super();
        this.status        = PatientStatus.REGISTERED;
        this.medicalHistory = new ArrayList<>();
    }

    public Patient(int id, String firstName, String lastName,
                   Gender gender, LocalDate dateOfBirth,
                   BloodGroup bloodGroup, String phone, String email) {
        super(id, firstName, lastName, gender, dateOfBirth, phone, email);
        this.bloodGroup     = bloodGroup;
        this.status         = PatientStatus.REGISTERED;
        this.medicalHistory = new ArrayList<>();
    }

    // ── Getters & Setters ────────────────────────────────────

    public BloodGroup getBloodGroup()              { return bloodGroup; }
    public void setBloodGroup(BloodGroup bg)        { this.bloodGroup = bg; }

    public PatientStatus getStatus()               { return status; }
    public void setStatus(PatientStatus s)          { this.status = s; }

    public int getAssignedDoctorId()               { return assignedDoctorId; }
    public void setAssignedDoctorId(int id)         { this.assignedDoctorId = id; }

    public String getEmergencyContactName()        { return emergencyContactName; }
    public void setEmergencyContactName(String n)   { this.emergencyContactName = n; }

    public String getEmergencyContactPhone()       { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String p)  { this.emergencyContactPhone = p; }

    public Address getAddress()                    { return address; }
    public void setAddress(Address a)               { this.address = a; }

    public List<MedicalRecord> getMedicalHistory() { return medicalHistory; }

    // ── Business Methods ─────────────────────────────────────

    public void addMedicalRecord(MedicalRecord record) {
        this.medicalHistory.add(record);
    }

    public boolean isAdmitted() {
        return status == PatientStatus.ADMITTED;
    }

    // ── Abstract implementations ─────────────────────────────

    @Override
    public String getRole() {
        return "Patient";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Patient Details ===");
        super.displayInfo();
        System.out.println("Blood Group       : " + (bloodGroup != null ? bloodGroup : "Unknown"));
        System.out.println("Status            : " + status);
        System.out.println("Emergency Contact : " + emergencyContactName
                           + " (" + emergencyContactPhone + ")");
        if (address != null) {
            System.out.println("Address           : " + address.getFullAddress());
        }
    }

    @Override
    public String toString() {
        return String.format("Patient[ID=%d | %s | %s | %s | Status=%s]",
                getId(), getFullName(), getGender(),
                bloodGroup != null ? bloodGroup : "?", status);
    }
}

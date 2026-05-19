package com.hms.model.composition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MedicalRecord - Composition with Patient (dies with Patient).
 * Demonstrates: Composition relationship.
 */
public class MedicalRecord {
    private int       recordId;
    private int       patientId;
    private int       doctorId;
    private String    diagnosis;
    private String    treatment;
    private String    notes;
    private LocalDate recordDate;

    // Composition: prescriptions belong to this record
    private List<Prescription> prescriptions;

    public MedicalRecord() {
        this.prescriptions = new ArrayList<>();
        this.recordDate    = LocalDate.now();
    }

    public MedicalRecord(int recordId, int patientId, int doctorId,
                         String diagnosis, String treatment,
                         String notes, LocalDate recordDate) {
        this.recordId     = recordId;
        this.patientId    = patientId;
        this.doctorId     = doctorId;
        this.diagnosis    = diagnosis;
        this.treatment    = treatment;
        this.notes        = notes;
        this.recordDate   = recordDate;
        this.prescriptions = new ArrayList<>();
    }

    public int getRecordId()                       { return recordId; }
    public void setRecordId(int id)                { this.recordId = id; }

    public int getPatientId()                      { return patientId; }
    public void setPatientId(int id)               { this.patientId = id; }

    public int getDoctorId()                       { return doctorId; }
    public void setDoctorId(int id)                { this.doctorId = id; }

    public String getDiagnosis()                   { return diagnosis; }
    public void setDiagnosis(String d)             { this.diagnosis = d; }

    public String getTreatment()                   { return treatment; }
    public void setTreatment(String t)             { this.treatment = t; }

    public String getNotes()                       { return notes; }
    public void setNotes(String n)                 { this.notes = n; }

    public LocalDate getRecordDate()               { return recordDate; }
    public void setRecordDate(LocalDate d)          { this.recordDate = d; }

    public List<Prescription> getPrescriptions()   { return prescriptions; }
    public void addPrescription(Prescription p)    { this.prescriptions.add(p); }

    @Override
    public String toString() {
        return String.format("Record[ID=%d | Date=%s | Diagnosis=%s]",
                recordId, recordDate, diagnosis);
    }
}

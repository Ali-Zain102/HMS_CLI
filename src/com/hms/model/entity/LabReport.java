package com.hms.model.entity;

import com.hms.enums.LabStatus;
import java.time.LocalDate;

public class LabReport {
    private int       reportId;
    private int       patientId;
    private int       doctorId;
    private String    patientName;
    private String    testName;
    private LocalDate testDate;
    private String    result;
    private LocalDate resultDate;
    private LabStatus status;
    private double    cost;
    private String    notes;

    public LabReport() {
        this.status   = LabStatus.REQUESTED;
        this.testDate = LocalDate.now();
    }

    public int getReportId()                   { return reportId; }
    public void setReportId(int id)            { this.reportId = id; }
    public int getPatientId()                  { return patientId; }
    public void setPatientId(int id)           { this.patientId = id; }
    public int getDoctorId()                   { return doctorId; }
    public void setDoctorId(int id)            { this.doctorId = id; }
    public String getPatientName()             { return patientName; }
    public void setPatientName(String n)       { this.patientName = n; }
    public String getTestName()                { return testName; }
    public void setTestName(String n)          { this.testName = n; }
    public LocalDate getTestDate()             { return testDate; }
    public void setTestDate(LocalDate d)       { this.testDate = d; }
    public String getResult()                  { return result; }
    public void setResult(String r)            { this.result = r; }
    public LocalDate getResultDate()           { return resultDate; }
    public void setResultDate(LocalDate d)     { this.resultDate = d; }
    public LabStatus getStatus()               { return status; }
    public void setStatus(LabStatus s)         { this.status = s; }
    public double getCost()                    { return cost; }
    public void setCost(double c)              { this.cost = c; }
    public String getNotes()                   { return notes; }
    public void setNotes(String n)             { this.notes = n; }

    @Override
    public String toString() {
        return String.format("Lab[ID=%d | %s | %s | %s]",
                reportId, testName, testDate, status);
    }
}

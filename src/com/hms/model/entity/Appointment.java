package com.hms.model.entity;

import com.hms.enums.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {
    private int               appointmentId;
    private int               patientId;
    private int               doctorId;
    private String            patientName;   // for display
    private String            doctorName;    // for display
    private LocalDate         appointmentDate;
    private LocalTime         appointmentTime;
    private String            reason;
    private AppointmentStatus status;
    private String            notes;

    public Appointment() { this.status = AppointmentStatus.SCHEDULED; }

    public int getAppointmentId()                  { return appointmentId; }
    public void setAppointmentId(int id)           { this.appointmentId = id; }
    public int getPatientId()                      { return patientId; }
    public void setPatientId(int id)               { this.patientId = id; }
    public int getDoctorId()                       { return doctorId; }
    public void setDoctorId(int id)                { this.doctorId = id; }
    public String getPatientName()                 { return patientName; }
    public void setPatientName(String n)           { this.patientName = n; }
    public String getDoctorName()                  { return doctorName; }
    public void setDoctorName(String n)            { this.doctorName = n; }
    public LocalDate getAppointmentDate()          { return appointmentDate; }
    public void setAppointmentDate(LocalDate d)    { this.appointmentDate = d; }
    public LocalTime getAppointmentTime()          { return appointmentTime; }
    public void setAppointmentTime(LocalTime t)    { this.appointmentTime = t; }
    public String getReason()                      { return reason; }
    public void setReason(String r)                { this.reason = r; }
    public AppointmentStatus getStatus()           { return status; }
    public void setStatus(AppointmentStatus s)     { this.status = s; }
    public String getNotes()                       { return notes; }
    public void setNotes(String n)                 { this.notes = n; }

    @Override
    public String toString() {
        return String.format("Appt[ID=%d | %s %s | Dr.%s | %s]",
                appointmentId, appointmentDate, appointmentTime, doctorName, status);
    }
}

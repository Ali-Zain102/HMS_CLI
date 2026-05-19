package com.hms.exception;

public class PatientNotFoundException extends HospitalException {
    public PatientNotFoundException(int patientId) {
        super("Patient not found with ID: " + patientId, 1001);
    }
    public PatientNotFoundException(String message) {
        super(message, 1001);
    }
}

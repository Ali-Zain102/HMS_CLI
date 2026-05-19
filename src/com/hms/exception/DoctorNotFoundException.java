package com.hms.exception;

public class DoctorNotFoundException extends HospitalException {
    public DoctorNotFoundException(int doctorId) {
        super("Doctor not found with ID: " + doctorId, 1002);
    }
    public DoctorNotFoundException(String message) {
        super(message, 1002);
    }
}

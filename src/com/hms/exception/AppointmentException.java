package com.hms.exception;

public class AppointmentException extends HospitalException {
    public AppointmentException(String message) {
        super(message, 1004);
    }
}

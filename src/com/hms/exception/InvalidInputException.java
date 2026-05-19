package com.hms.exception;

public class InvalidInputException extends HospitalException {
    public InvalidInputException(String message) {
        super(message, 1006);
    }
}

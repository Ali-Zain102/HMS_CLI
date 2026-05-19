package com.hms.exception;

public class BedNotAvailableException extends HospitalException {
    public BedNotAvailableException(int bedId) {
        super("Bed is not available: " + bedId, 1003);
    }
    public BedNotAvailableException(String message) {
        super(message, 1003);
    }
}

package com.hms.exception;

// ============================================================
// Base exception for all HMS errors
// ============================================================
public class HospitalException extends Exception {
    private final int errorCode;

    public HospitalException(String message) {
        super(message);
        this.errorCode = 0;
    }

    public HospitalException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public HospitalException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = 0;
    }

    public int getErrorCode() {
        return errorCode;
    }
}

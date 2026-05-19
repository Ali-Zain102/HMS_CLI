package com.hms.exception;

public class BillingException extends HospitalException {
    public BillingException(String message) {
        super(message, 1005);
    }
}

package com.hms.interfaces;

public interface Payable {
    boolean processPayment(double amount, String method);
    double getOutstandingBalance();
}

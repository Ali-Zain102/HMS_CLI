package com.hms.model.entity;

import com.hms.enums.PaymentMethod;
import java.time.LocalDateTime;

public class Payment {
    private int           paymentId;
    private int           billId;
    private double        amountPaid;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    private String        referenceNo;
    private String        notes;

    public Payment() { this.paymentDate = LocalDateTime.now(); }

    public int getPaymentId()                      { return paymentId; }
    public void setPaymentId(int id)               { this.paymentId = id; }
    public int getBillId()                         { return billId; }
    public void setBillId(int id)                  { this.billId = id; }
    public double getAmountPaid()                  { return amountPaid; }
    public void setAmountPaid(double a)            { this.amountPaid = a; }
    public LocalDateTime getPaymentDate()          { return paymentDate; }
    public void setPaymentDate(LocalDateTime d)    { this.paymentDate = d; }
    public PaymentMethod getPaymentMethod()        { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod m)  { this.paymentMethod = m; }
    public String getReferenceNo()                 { return referenceNo; }
    public void setReferenceNo(String r)           { this.referenceNo = r; }
    public String getNotes()                       { return notes; }
    public void setNotes(String n)                 { this.notes = n; }

    @Override
    public String toString() {
        return String.format("Payment[ID=%d | Bill=%d | PKR %.2f | %s | %s]",
                paymentId, billId, amountPaid, paymentMethod, paymentDate);
    }
}

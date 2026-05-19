package com.hms.model.entity;

import com.hms.enums.BillStatus;
import java.time.LocalDate;

public class Bill {
    private int        billId;
    private int        patientId;
    private String     patientName;   // for display
    private LocalDate  billDate;
    private double     consultationFee;
    private double     bedCharges;
    private double     medicineCharges;
    private double     labCharges;
    private double     otherCharges;
    private double     discount;
    private double     totalAmount;
    private BillStatus status;
    private String     notes;

    public Bill() {
        this.status   = BillStatus.PENDING;
        this.billDate = LocalDate.now();
    }

    public int getBillId()                     { return billId; }
    public void setBillId(int id)              { this.billId = id; }
    public int getPatientId()                  { return patientId; }
    public void setPatientId(int id)           { this.patientId = id; }
    public String getPatientName()             { return patientName; }
    public void setPatientName(String n)       { this.patientName = n; }
    public LocalDate getBillDate()             { return billDate; }
    public void setBillDate(LocalDate d)       { this.billDate = d; }
    public double getConsultationFee()         { return consultationFee; }
    public void setConsultationFee(double f)   { this.consultationFee = f; }
    public double getBedCharges()              { return bedCharges; }
    public void setBedCharges(double c)        { this.bedCharges = c; }
    public double getMedicineCharges()         { return medicineCharges; }
    public void setMedicineCharges(double c)   { this.medicineCharges = c; }
    public double getLabCharges()              { return labCharges; }
    public void setLabCharges(double c)        { this.labCharges = c; }
    public double getOtherCharges()            { return otherCharges; }
    public void setOtherCharges(double c)      { this.otherCharges = c; }
    public double getDiscount()                { return discount; }
    public void setDiscount(double d)          { this.discount = d; }
    public double getTotalAmount()             { return totalAmount; }
    public void setTotalAmount(double t)       { this.totalAmount = t; }
    public BillStatus getStatus()              { return status; }
    public void setStatus(BillStatus s)        { this.status = s; }
    public String getNotes()                   { return notes; }
    public void setNotes(String n)             { this.notes = n; }

    public double calculateTotal() {
        return consultationFee + bedCharges + medicineCharges
             + labCharges + otherCharges - discount;
    }

    @Override
    public String toString() {
        return String.format("Bill[ID=%d | Patient=%d | Total=PKR %.2f | %s]",
                billId, patientId, totalAmount, status);
    }
}

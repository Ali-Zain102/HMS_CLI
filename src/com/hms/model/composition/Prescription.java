package com.hms.model.composition;

/**
 * Prescription - Composition with MedicalRecord.
 */
public class Prescription {
    private int    prescriptionId;
    private int    recordId;
    private int    medicineId;
    private String medicineName;   // denormalized for display
    private String dosage;
    private String frequency;
    private int    durationDays;
    private int    quantity;

    public Prescription() {}

    public Prescription(int recordId, int medicineId, String medicineName,
                        String dosage, String frequency, int durationDays, int quantity) {
        this.recordId     = recordId;
        this.medicineId   = medicineId;
        this.medicineName = medicineName;
        this.dosage       = dosage;
        this.frequency    = frequency;
        this.durationDays = durationDays;
        this.quantity     = quantity;
    }

    public int getPrescriptionId()            { return prescriptionId; }
    public void setPrescriptionId(int id)     { this.prescriptionId = id; }

    public int getRecordId()                  { return recordId; }
    public void setRecordId(int id)           { this.recordId = id; }

    public int getMedicineId()                { return medicineId; }
    public void setMedicineId(int id)         { this.medicineId = id; }

    public String getMedicineName()           { return medicineName; }
    public void setMedicineName(String n)     { this.medicineName = n; }

    public String getDosage()                 { return dosage; }
    public void setDosage(String d)           { this.dosage = d; }

    public String getFrequency()              { return frequency; }
    public void setFrequency(String f)        { this.frequency = f; }

    public int getDurationDays()              { return durationDays; }
    public void setDurationDays(int d)        { this.durationDays = d; }

    public int getQuantity()                  { return quantity; }
    public void setQuantity(int q)            { this.quantity = q; }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %d days | Qty: %d",
                medicineName, dosage, frequency, durationDays, quantity);
    }
}

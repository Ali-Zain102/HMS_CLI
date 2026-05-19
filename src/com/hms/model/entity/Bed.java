package com.hms.model.entity;

import com.hms.enums.BedStatus;
import com.hms.enums.BedType;

public class Bed {
    private int       bedId;
    private String    bedNumber;
    private String    ward;
    private BedType   bedType;
    private BedStatus status;
    private double    dailyRate;
    private int       patientId;   // 0 = unassigned

    public Bed() { this.status = BedStatus.AVAILABLE; }

    public int getBedId()                  { return bedId; }
    public void setBedId(int id)           { this.bedId = id; }
    public String getBedNumber()           { return bedNumber; }
    public void setBedNumber(String n)     { this.bedNumber = n; }
    public String getWard()                { return ward; }
    public void setWard(String w)          { this.ward = w; }
    public BedType getBedType()            { return bedType; }
    public void setBedType(BedType t)      { this.bedType = t; }
    public BedStatus getStatus()           { return status; }
    public void setStatus(BedStatus s)     { this.status = s; }
    public double getDailyRate()           { return dailyRate; }
    public void setDailyRate(double r)     { this.dailyRate = r; }
    public int getPatientId()              { return patientId; }
    public void setPatientId(int id)       { this.patientId = id; }

    public boolean isAvailable() { return status == BedStatus.AVAILABLE; }

    @Override
    public String toString() {
        return String.format("Bed[%s | %s | %s | PKR %.0f/day | Status=%s]",
                bedNumber, ward, bedType, dailyRate, status);
    }
}

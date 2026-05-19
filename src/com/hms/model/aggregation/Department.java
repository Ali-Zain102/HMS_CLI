package com.hms.model.aggregation;

/**
 * Department - Aggregation with Doctor/Nurse.
 * Department exists independently of staff members.
 * Demonstrates: Aggregation relationship.
 */
public class Department {

    private int    departmentId;
    private String name;
    private String location;
    private String phone;

    public Department() {}

    public Department(int departmentId, String name, String location, String phone) {
        this.departmentId = departmentId;
        this.name         = name;
        this.location     = location;
        this.phone        = phone;
    }

    public int getDepartmentId()           { return departmentId; }
    public void setDepartmentId(int id)    { this.departmentId = id; }

    public String getName()                { return name; }
    public void setName(String n)          { this.name = n; }

    public String getLocation()            { return location; }
    public void setLocation(String l)      { this.location = l; }

    public String getPhone()               { return phone; }
    public void setPhone(String p)         { this.phone = p; }

    @Override
    public String toString() {
        return String.format("Dept[ID=%d | %s | %s]", departmentId, name, location);
    }
}

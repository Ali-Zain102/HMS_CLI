package com.hms.model.entity;

import java.time.LocalDate;

public class Medicine {
    private int       medicineId;
    private String    name;
    private String    category;
    private double    unitPrice;
    private int       stockQuantity;
    private int       reorderLevel;
    private LocalDate expiryDate;
    private String    manufacturer;

    public Medicine() {}

    public int getMedicineId()                 { return medicineId; }
    public void setMedicineId(int id)          { this.medicineId = id; }
    public String getName()                    { return name; }
    public void setName(String n)              { this.name = n; }
    public String getCategory()                { return category; }
    public void setCategory(String c)          { this.category = c; }
    public double getUnitPrice()               { return unitPrice; }
    public void setUnitPrice(double p)         { this.unitPrice = p; }
    public int getStockQuantity()              { return stockQuantity; }
    public void setStockQuantity(int q)        { this.stockQuantity = q; }
    public int getReorderLevel()               { return reorderLevel; }
    public void setReorderLevel(int r)         { this.reorderLevel = r; }
    public LocalDate getExpiryDate()           { return expiryDate; }
    public void setExpiryDate(LocalDate d)     { this.expiryDate = d; }
    public String getManufacturer()            { return manufacturer; }
    public void setManufacturer(String m)      { this.manufacturer = m; }

    public boolean isLowStock()   { return stockQuantity <= reorderLevel; }
    public boolean isOutOfStock() { return stockQuantity == 0; }

    @Override
    public String toString() {
        return String.format("Medicine[%s | %s | PKR %.2f | Stock=%d]",
                name, category, unitPrice, stockQuantity);
    }
}

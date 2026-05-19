package com.hms.model.composition;

/**
 * Address - Composition with Patient.
 * Cannot exist without a Patient.
 */
public class Address {
    private String street;
    private String city;
    private String province;
    private String postalCode;

    public Address() {}

    public Address(String street, String city, String province, String postalCode) {
        this.street     = street;
        this.city       = city;
        this.province   = province;
        this.postalCode = postalCode;
    }

    public String getStreet()             { return street; }
    public void setStreet(String s)       { this.street = s; }

    public String getCity()               { return city; }
    public void setCity(String c)         { this.city = c; }

    public String getProvince()           { return province; }
    public void setProvince(String p)     { this.province = p; }

    public String getPostalCode()         { return postalCode; }
    public void setPostalCode(String pc)  { this.postalCode = pc; }

    public String getFullAddress() {
        return street + ", " + city + ", " + province
               + (postalCode != null ? " " + postalCode : "");
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}

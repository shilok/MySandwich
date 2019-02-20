package com.sk.mysandwichv2.mill;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ShippingAddress implements Serializable {
    private String fullName;
    private String street;
    private String city;
    private String phone;
    private boolean selectedAsDefault;

    public ShippingAddress() {
    }

    public ShippingAddress(String fullName, String street, String city, String phone, boolean selectedAsDefault) {
        this.fullName = fullName;
        this.street = street;
        this.city = city;
        this.phone = phone;
        this.selectedAsDefault = selectedAsDefault;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isSelectedAsDefault() {
        return selectedAsDefault;
    }

    public void setSelectedAsDefault(boolean selectedAsDefault) {
        this.selectedAsDefault = selectedAsDefault;
    }

    @Override
    public String toString() {
        return "ShippingAddress{" +
                "fullName='" + fullName + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", phone='" + phone + '\'' +
                ", selectedAsDefault=" + selectedAsDefault +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fullName", fullName);
        result.put("street", street);
        result.put("city", city);
        result.put("phone", phone);
        result.put("selectedAsDefault", selectedAsDefault);

        return result;
    }
}

package com.sk.mysandwichv2.mill;

import java.io.Serializable;
import java.util.List;

public class OrderDetails implements Serializable {
    private List<Mill> mills;
    private ShippingAddress address;
    private String status;

    public String getStatus() {
        return status;
    }

    public OrderDetails(List<Mill> mills, ShippingAddress address, String status) {
        this.mills = mills;
        this.address = address;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderDetails(List<Mill> mills, ShippingAddress address) {
        this.mills = mills;
        this.address = address;
    }

    public OrderDetails(List<Mill> mills) {
        this.mills = mills;
    }

    public OrderDetails() {
    }

    public void setMills(List<Mill> mills) {
        this.mills = mills;
    }

    public void setAddress(ShippingAddress address) {
        this.address = address;
    }

    public List<Mill> getMills() {
        return mills;
    }

    public ShippingAddress getAddress() {
        return address;
    }
}



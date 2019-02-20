package com.sk.mysandwichv2.interfaces;

import com.sk.mysandwichv2.mill.ShippingAddress;

import java.util.List;

public interface FirebaseAddressesListener {
    void onCallBack(List<ShippingAddress> list);
}

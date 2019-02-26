package com.sk.mysandwichv2.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.interfaces.FirebaseAddressesListener;
import com.sk.mysandwichv2.mill.ShippingAddress;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class DialogFragmentAddress extends DialogFragment implements Serializable, FirebaseAddressesListener {
    private View view;
    private EditText etFullName, etPhone, etCity, etStreet;
    private Switch swDefaultAddress;
    private Button btnSave;
    private String uid = FirebaseAuth.getInstance().getUid();
    private DatabaseReference mAddressRef;
    private List<ShippingAddress> addresses;
    private boolean changeDefault = false;



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.address_details, container, false);
        mAddressRef = FirebaseDatabase.getInstance().getReference().child("addresses").child(uid);
        addresses = new ArrayList<>();
        setIds();
        btnSave.setOnClickListener(btnSaveListener);

        mAddressRef.addListenerForSingleValueEvent(addressEventListener);
        return view;
    }


    private void setIds() {
        etFullName = view.findViewById(R.id.etFullName);
        etPhone = view.findViewById(R.id.etPhone);
        etCity = view.findViewById(R.id.etCity);
        etStreet = view.findViewById(R.id.etStreet);
        btnSave = view.findViewById(R.id.btnSave);
        swDefaultAddress = view.findViewById(R.id.switchDefault);
    }

    View.OnClickListener btnSaveListener = v -> {
        if (getFullName().isEmpty()) {
            etFullName.setError("שדה חובה");
            return;
        }
        if (getStreet().isEmpty()) {
            etStreet.setError("שדה חובה");
        }
        if (getCity().isEmpty()) {
            etCity.setError("שדה חובה");
            return;
        }
        if (getPhone().isEmpty()) {
            etPhone.setError("שדה חובה");
            return;
        }
        if (isDefaultAddress()) {
            changeDefault = true;
        }
        ShippingAddress shippingAddress = new ShippingAddress(getFullName(), getStreet(), getCity(), getPhone(), isDefaultAddress());
        addNewAddress(shippingAddress);
        clearEt();
        dismiss();
    };

    private void addNewAddress(ShippingAddress shippingAddress) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("addresses").child(uid);
        if (changeDefault){
            for (ShippingAddress address : addresses) {
                if (address.isSelectedAsDefault()) {
                    address.setSelectedAsDefault(false);
                }
            }
        }
        changeDefault = false;
        addresses.add(shippingAddress);
        reference.setValue(addresses);
    }



    private ValueEventListener addressEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            addresses.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                ShippingAddress address = snapshot.getValue(ShippingAddress.class);
                addresses.add(address);
            }
            onCallBack(addresses);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void clearEt() {
        etFullName.setText("");
        etPhone.setText("");
        etCity.setText("");
        etStreet.setText("");
        swDefaultAddress.setChecked(false);

    }

    private String getFullName() {
        return etFullName.getText().toString();
    }


    private String getPhone() {
        return etPhone.getText().toString();
    }


    private String getCity() {
        return etCity.getText().toString();
    }

    private String getStreet() {
        return etStreet.getText().toString();
    }

    private boolean isDefaultAddress() {
        return swDefaultAddress.isChecked();
    }


    @Override
    public void onCallBack(List<ShippingAddress> list) {

    }
}


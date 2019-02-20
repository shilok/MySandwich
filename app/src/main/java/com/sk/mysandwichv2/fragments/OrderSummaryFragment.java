package com.sk.mysandwichv2.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.SummaryRecyclerAdapter;
import com.sk.mysandwichv2.interfaces.FirebaseAddressesListener;
import com.sk.mysandwichv2.mill.Mill;
import com.sk.mysandwichv2.mill.OrderDetails;
import com.sk.mysandwichv2.mill.ShippingAddress;

import java.util.ArrayList;
import java.util.List;


public class OrderSummaryFragment extends Fragment implements FirebaseAddressesListener {
    public final static String TAG = "Log Summary Fragment";
    private List<Mill> mills;
    private OrderDetails orderDetails;
    private List<ShippingAddress> addressList;
    private TextView tvFullName, tvPhone, tvAddress, tvDefaultAddress;
    private ShippingAddress shippingAddress;
    private RecyclerView rvSummary;
    private SummaryRecyclerAdapter adapter;
    private ConstraintLayout clNewAddress, clAddressLayout;
    private CoordinatorLayout summaryLayout;
    private boolean fromCode = true;
    private AddressFragment addressFragment;
    private String uid = FirebaseAuth.getInstance().getUid();
    private DatabaseReference ref;


    private IntentFilter addressSelected = new IntentFilter("addressSelected");


    public static OrderSummaryFragment newInstance(OrderDetails orderDetails) {
        Bundle args = new Bundle();
        args.putSerializable("orderDetails", orderDetails);
        OrderSummaryFragment fragment = new OrderSummaryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_summary, container, false);

        addressList = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference().child("addresses").child(uid);
        ref.addValueEventListener(eventListener);

        setIDs(view);
        clNewAddress.setAlpha(0.0f);
        clAddressLayout.setAlpha(0.0f);


        if (getArguments() != null) {
            orderDetails = (OrderDetails) getArguments().getSerializable("orderDetails");
            mills = orderDetails.getMills();

            adapter = new SummaryRecyclerAdapter(mills, getContext());
            rvSummary.setLayoutManager(new LinearLayoutManager(getContext()));
            rvSummary.setAdapter(adapter);
        }

        return view;
    }

    private void setIDs(View view) {
        summaryLayout = view.findViewById(R.id.summaryLayout);
        rvSummary = view.findViewById(R.id.rvSummary);
        clNewAddress = view.findViewById(R.id.clNewAddress);
        clAddressLayout = view.findViewById(R.id.clAddressLayout);
        tvFullName = view.findViewById(R.id.fullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvDefaultAddress = view.findViewById(R.id.tvDefaultAddress);
    }


    private void updateAddressOnLayout(ShippingAddress shippingAddress) {
        if (shippingAddress.isSelectedAsDefault()) {
            tvDefaultAddress.setVisibility(View.VISIBLE);
        } else {
            tvDefaultAddress.setVisibility(View.GONE);
        }
        tvFullName.setText(shippingAddress.getFullName());
        tvPhone.setText(shippingAddress.getPhone());
        tvAddress.setText(shippingAddress.getStreet() + "\n" + shippingAddress.getCity());
    }


    private void setAddressOnLayout(List<ShippingAddress> list) {
        if (orderDetails.getAddress() != null) {

            clNewAddress.setVisibility(View.INVISIBLE);
            clAddressLayout.setVisibility(View.VISIBLE);
            clAddressLayout.animate().alpha(1.0f).setDuration(300);
            updateAddressOnLayout(orderDetails.getAddress());

            clAddressLayout.setOnClickListener(v -> {
                addressFragment = AddressFragment.newInstance(true, orderDetails);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_container, addressFragment)
                        .commit();
            });

        } else if (list.size() > 0) {

            clNewAddress.setVisibility(View.INVISIBLE);
            clAddressLayout.setVisibility(View.VISIBLE);
            clAddressLayout.animate().alpha(1.0f).setDuration(300);

            clAddressLayout.setOnClickListener(v -> {
                addressFragment = AddressFragment.newInstance(true, orderDetails);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_container, addressFragment, "addressFragment")
                        .commit();
            });

            boolean isHaveDefault = false;
            for (ShippingAddress address : list) {
                if (address.isSelectedAsDefault()) {
                    isHaveDefault = true;
                    updateAddressOnLayout(address);
                }
            }
            if (!isHaveDefault) {
                int lastIndex = list.size() - 1;
                updateAddressOnLayout(list.get(lastIndex));
            }

        } else {
            clAddressLayout.setVisibility(View.INVISIBLE);
            clNewAddress.setVisibility(View.VISIBLE);
            clNewAddress.animate().alpha(1.0f).setDuration(300);
            clNewAddress.setOnClickListener(v -> {
                addressFragment = AddressFragment.newInstance(false, orderDetails);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, addressFragment).commit();
            });

        }

    }



    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            addressList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                ShippingAddress value = snapshot.getValue(ShippingAddress.class);
                addressList.add(value);
            }
            onCallBack(addressList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    @Override
    public void onCallBack(List<ShippingAddress> list) {
        this.addressList = list;
        setAddressOnLayout(list);
    }

    @Override
    public void onStop() {
        super.onStop();
        ref.removeEventListener(eventListener);
    }

//    private void setAddressSelectedListener() {
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                ShippingAddress shippingAddress = (ShippingAddress) intent.getSerializableExtra("address");
//                updateAddressOnLayout(shippingAddress);
//                summaryLayout.setVisibility(View.VISIBLE);
//
//            }
//        }, addressSelected);
//    }


}

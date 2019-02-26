package com.sk.mysandwichv2.fragments;


import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.DrinkRecyclerAdapter;
import com.sk.mysandwichv2.adapters.SummaryRecyclerAdapter;
import com.sk.mysandwichv2.interfaces.FirebaseAddressesListener;
import com.sk.mysandwichv2.mill.Drinks;
import com.sk.mysandwichv2.mill.Ingredients;
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
    private TextView tvFullName, tvPhone, tvAddress, tvDefaultAddress, tvAddAddress;
    private TextView tvMills, tvShipping, tvTotalPrice;
    private RecyclerView rvSummary;
    private SummaryRecyclerAdapter adapter;
    private ConstraintLayout clNewAddress, clAddressLayout;
    private AddressFragment addressFragment;
    private String uid = FirebaseAuth.getInstance().getUid();
    private DatabaseReference ref;
    private int shippingPrice = 0;
    private String strMills, strShipping, strTotalPrice, strFreeShipping;
    private FrameLayout flAddress;
    private Button btnPlaceOrder;
    private CoordinatorLayout summaryLayout, snackView;
    private View view;
    private DatabaseReference orderRef;




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
        view = inflater.inflate(R.layout.fragment_order_summary, container, false);
        setIDs(view);

        addressList = new ArrayList<>();
        orderRef = FirebaseDatabase.getInstance().getReference().child("order").child(uid);

        ref = FirebaseDatabase.getInstance().getReference().child("addresses").child(uid);
        ref.addValueEventListener(eventListener);

        clNewAddress.setAlpha(0.0f);
        clAddressLayout.setAlpha(0.0f);
        btnPlaceOrder.setOnClickListener(onMakeOrderBTNListener);

        if (getArguments() != null) {
            orderDetails = (OrderDetails) getArguments().getSerializable("orderDetails");

            mills = orderDetails.getMills();
            updateSummaryLayout(orderDetails);
            adapter = new SummaryRecyclerAdapter(mills, getContext());
            rvSummary.setLayoutManager(new LinearLayoutManager(getContext()));
            rvSummary.setAdapter(adapter);
        }

        return view;
    }

    private void setIDs(View view) {
        flAddress = view.findViewById(R.id.flAddress);
        rvSummary = view.findViewById(R.id.rvSummary);
        clNewAddress = view.findViewById(R.id.clNewAddress);
        clAddressLayout = view.findViewById(R.id.clAddressLayout);
        tvFullName = view.findViewById(R.id.fullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvDefaultAddress = view.findViewById(R.id.tvDefaultAddress);
        tvMills = view.findViewById(R.id.tvMills);
        tvShipping = view.findViewById(R.id.tvShipping);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        strMills = getResources().getString(R.string.mills);
        strShipping = getResources().getString(R.string.shipping);
        strTotalPrice = getResources().getString(R.string.tvTotalPrice);
        strFreeShipping = getResources().getString(R.string.freeShipping);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);
        tvAddAddress = view.findViewById(R.id.tvAddAddress);
        summaryLayout = view.findViewById(R.id.bottom_frame);
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

    private void updateSummaryLayout(OrderDetails orderDetails){
        if (orderDetails.getMills() != null) {
            tvMills.setText(strMills + " " + "₪" + getTotalMillsPrice(orderDetails.getMills()));
            if (orderDetails.getMills().size() > 1){
                tvShipping.setText(strShipping + " " + strFreeShipping);
                tvTotalPrice.setText(strTotalPrice + " " + "₪" + getTotalMillsPrice(orderDetails.getMills()));
            }else {
                int totalPrice = 0;
                shippingPrice = 10;
                tvShipping.setText(strShipping + " " + shippingPrice);
                totalPrice = shippingPrice + getTotalMillsPrice(orderDetails.getMills());
                tvTotalPrice.setText(strTotalPrice + " " + "₪" + totalPrice);
            }
        }

    }

    private int getTotalMillsPrice(List<Mill> mills){
        int price = 0;
        for (Mill mill : mills) {
            price += mill.getPrice();
        }
        return price;
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
                        .replace(R.id.main_container, addressFragment, "addressFragment")
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
                    orderDetails.setAddress(address);
                    updateAddressOnLayout(address);
                }
            }
            if (!isHaveDefault) {
                int lastIndex = list.size() - 1;
                updateAddressOnLayout(list.get(lastIndex));
                orderDetails.setAddress(list.get(lastIndex));
            }

        } else {
            clAddressLayout.setVisibility(View.INVISIBLE);
            clNewAddress.setVisibility(View.VISIBLE);
            clNewAddress.animate().alpha(1.0f).setDuration(300);
            clNewAddress.setOnClickListener(v -> {
                addressFragment = AddressFragment.newInstance(false, orderDetails);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.main_container, addressFragment, "addressFragment").commit();
            });

        }

    }

    View.OnClickListener onMakeOrderBTNListener = v -> {

        if (orderDetails.getAddress() == null){
            tvAddAddress.requestFocus();

            Snackbar snackbar = Snackbar.make(summaryLayout, "אנא בחר כתובת", Snackbar.LENGTH_LONG);
            snackbar.show();

        }else {
            DatabaseReference push = orderRef.push();
            push.setValue(cleanMillsForDB(orderDetails));

        }
    };

    private OrderDetails cleanMillsForDB(OrderDetails orderDetails){
        List<Mill> tempList = new ArrayList<>();
        for (Mill mill : orderDetails.getMills()) {
            List<Ingredients> ingredients = new ArrayList<>();
            List<Drinks> drinks = new ArrayList<>();

            for (Ingredients ingredient : mill.getIngredients()) {
                if (ingredient.isSelected){
                    ingredients.add(ingredient);
                }
            }

            for (Drinks drink : mill.getDrinks()) {
                if (drink.isSelected()){
                    drinks.add(drink);
                }
            }
            Mill tempMill = new Mill(mill.getName(), mill.getDescription(), mill.getPrice(),
                    null, true, ingredients, drinks, mill.getCategory());
            tempList.add(tempMill);
        }
        orderDetails.setMills(tempList);
        return orderDetails;
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
        if (list.size() == 0){
            orderDetails.setAddress(null);
        }
        this.addressList = list;
        setAddressOnLayout(list);
    }

    @Override
    public void onStop() {
        super.onStop();
        ref.removeEventListener(eventListener);
    }


}

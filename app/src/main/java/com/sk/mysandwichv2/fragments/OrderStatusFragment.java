package com.sk.mysandwichv2.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.MillRecyclerAdapter;
import com.sk.mysandwichv2.adapters.OrderStatusRecyclerAdapter;
import com.sk.mysandwichv2.mill.Mill;
import com.sk.mysandwichv2.mill.OrderDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderStatusFragment extends Fragment {
    private View view;
    private DatabaseReference orderRef;
    private String uid;
    private RecyclerView rv;
    private OrderStatusRecyclerAdapter adapter;

    public OrderStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_status, container, false);
        rv = view.findViewById(R.id.ordersRV);

        uid = FirebaseAuth.getInstance().getUid();
        orderRef = FirebaseDatabase.getInstance().getReference().child("order").child(uid);


        setAdapter();

        return view;
    }



    private void setAdapter(){
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("order")
                .child(uid);

        FirebaseRecyclerOptions<OrderDetails> options = new FirebaseRecyclerOptions.Builder<OrderDetails>()
                .setQuery(query, OrderDetails.class)
                .build();
        adapter = new OrderStatusRecyclerAdapter(options);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }


}

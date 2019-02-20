package com.sk.mysandwichv2.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.AddressRecyclerAdapter;
import com.sk.mysandwichv2.mill.OrderDetails;
import com.sk.mysandwichv2.mill.ShippingAddress;


public class AddressFragment extends Fragment {
    private RecyclerView rv;
    private View view;
    private TextView tvAddAddress;
    private Paint p = new Paint();
    private OrderDetails orderDetails;
    private DialogFragmentAddress dialogFragmentAddress = new DialogFragmentAddress();

    private AddressRecyclerAdapter adapter;
    private String uid = FirebaseAuth.getInstance().getUid();


    public static AddressFragment newInstance(boolean isHaveAddress, OrderDetails orderDetails) {
        Bundle args = new Bundle();
        args.putBoolean("isHaveAddress", isHaveAddress);
        args.putSerializable("orderDetails", orderDetails);
        AddressFragment fragment = new AddressFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_address_recycler, container, false);
        rv = view.findViewById(R.id.rvAddress);
        tvAddAddress = view.findViewById(R.id.tvAddAddress);

        if (getArguments() != null) {
            orderDetails = (OrderDetails) getArguments().getSerializable("orderDetails");
            boolean isHaveAddress = getArguments().getBoolean("isHaveAddress");
            if (!isHaveAddress){
                dialogFragmentAddress.show(getFragmentManager(), "dialogAddressFragment");
            }
        }

        tvAddAddress.setOnClickListener(v -> dialogFragmentAddress.show(getFragmentManager(), "dialogAddressFragment"));

        setupRecycler();
        setupSwipe();


        return view;
    }

    private void setupRecycler() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("addresses")
                .child(uid);


        FirebaseRecyclerOptions<ShippingAddress> options =
                new FirebaseRecyclerOptions.Builder<ShippingAddress>()
                        .setQuery(query, ShippingAddress.class)
                        .build();

        adapter = new AddressRecyclerAdapter(options, getActivity(), orderDetails);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setupSwipe() {


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                if (direction == ItemTouchHelper.LEFT) {
                    adapter.getRef(position).removeValue();
                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {


                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX < 0) {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF(
                                (float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,
                                (float) itemView.getBottom() - width);

                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }


}

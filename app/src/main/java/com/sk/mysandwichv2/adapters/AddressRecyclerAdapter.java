package com.sk.mysandwichv2.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.fragments.OrderSummaryFragment;
import com.sk.mysandwichv2.mill.Mill;
import com.sk.mysandwichv2.mill.OrderDetails;
import com.sk.mysandwichv2.mill.ShippingAddress;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

public class AddressRecyclerAdapter extends FirebaseRecyclerAdapter<ShippingAddress, AddressRecyclerAdapter.ViewHolder> {

    private Context context;
    private OrderDetails orderDetails;
    private Intent addressSelected = new Intent("addressSelected");


    public AddressRecyclerAdapter(@NonNull FirebaseRecyclerOptions<ShippingAddress> options, Context context, OrderDetails orderDetails) {
        super(options);
        this.orderDetails = orderDetails;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShippingAddress model) {

        String firstLastName = model.getFullName();
        String address = model.getStreet() + ", " + model.getCity();
        String phone = model.getPhone();

        if (model.isSelectedAsDefault()){
            holder.tvDefaultAddress.setVisibility(View.VISIBLE);
        }else {
            holder.tvDefaultAddress.setVisibility(View.GONE);
        }

        holder.fullName.setText(firstLastName);
        holder.address.setText(address);
        holder.phone.setText(phone);

        holder.itemView.setOnClickListener(v -> {
            orderDetails.setAddress(model);
            OrderSummaryFragment summaryFragment = OrderSummaryFragment.newInstance(orderDetails);
            ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_container, summaryFragment, "summaryFragment")
                    .commit();
//
//            addressSelected.putExtra("address", model);
//            LocalBroadcastManager.getInstance(context).sendBroadcast(addressSelected);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_address_view, viewGroup, false);
        return new ViewHolder(view);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, address, phone,tvDefaultAddress;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            ivDelete = itemView.findViewById(R.id.ivDeleteAddress);
            tvDefaultAddress = itemView.findViewById(R.id.tvDefaultAddress);

        }
    }
}

package com.sk.mysandwichv2.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.MillRecyclerAdapter;
import com.sk.mysandwichv2.mill.Mill;

/**
 * A simple {@link Fragment} subclass.
 */
public class RvFragment extends Fragment {
    public final static String TAG = "Log RvFragment";
    RecyclerView rv;
    MillRecyclerAdapter adapter;


    public static RvFragment newInstance(String category) {

        Bundle args = new Bundle();
        args.putString("category", category);
        RvFragment fragment = new RvFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rv, container, false);


        rv = view.findViewById(R.id.rv);

        if (getArguments() != null) {
            String category = getArguments().getString("category");
            setAdapter(category);
        }
        return view;
    }

    private void setAdapter(String category){
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("mill")
                .orderByChild("category")
                .equalTo(category);

        FirebaseRecyclerOptions<Mill> options = new FirebaseRecyclerOptions.Builder<Mill>()
                .setQuery(query, Mill.class)
                .build();
        adapter = new MillRecyclerAdapter(options, getActivity());
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
}

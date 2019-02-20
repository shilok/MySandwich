package com.sk.mysandwichv2.fragments;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.transition.Fade;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.CategoryViewAdapter;
import com.sk.mysandwichv2.interfaces.FirebaseCategoriesCallBack;
import com.sk.mysandwichv2.mill.Category;

import java.util.ArrayList;
import java.util.List;


public class CategoryViewFragment extends Fragment implements FirebaseCategoriesCallBack {

    public final static String TAG = "Log PagerView Fragment";
    private List<Category> categories;
    private ViewPager pager;
    private CategoryViewAdapter categoryViewAdapter;
    private TabLayout tabs;
    private Intent enterToViewFragment = new Intent("enterToViewFragment");
    private Intent exitFromViewFragment = new Intent("exitFromViewFragment");



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_view, container, false);
        Fade exitFade = new Fade();
        exitFade.setDuration(750);
        setExitTransition(exitFade);

        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(R.id.tabs);

        readCategoriesData();
        pager.setAdapter(categoryViewAdapter);
        tabs.setupWithViewPager(pager);

        return view;
    }

    private void readCategoriesData(){


        categories = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Categories");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category value = snapshot.getValue(Category.class);
                    categories.add(value);
                }
                onCallBack(categories);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCallBack(List<Category> list) {
        categoryViewAdapter = new CategoryViewAdapter(getActivity().getSupportFragmentManager(), list);
        pager.setAdapter(categoryViewAdapter);
        tabs.setupWithViewPager(pager);

        onCallBackDone();
    }

    private void onCallBackDone() {
        Intent intent = new Intent("OnCallBackDone");
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(enterToViewFragment);
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(exitFromViewFragment);
        super.onPause();
    }
}

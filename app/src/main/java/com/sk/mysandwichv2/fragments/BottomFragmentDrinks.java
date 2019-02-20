package com.sk.mysandwichv2.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.DrinkRecyclerAdapter;
import com.sk.mysandwichv2.mill.Mill;

public class BottomFragmentDrinks extends BottomSheetDialogFragment {
    RecyclerView rvDrinks;

    public static BottomFragmentDrinks newInstance(Mill mill) {

        Bundle args = new Bundle();
        args.putSerializable("mill", mill);
        BottomFragmentDrinks fragment = new BottomFragmentDrinks();
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addons_rv, container, false);
        rvDrinks = view.findViewById(R.id.rv_addons);
        DrinkRecyclerAdapter adapter = new DrinkRecyclerAdapter(getActivity(), (Mill) getArguments().getSerializable("mill"));

        rvDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDrinks.setAdapter(adapter);
        return view;
    }


}

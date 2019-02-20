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
import com.sk.mysandwichv2.adapters.IngredientRecyclerAdapter;
import com.sk.mysandwichv2.mill.Mill;

public class BottomFragmentIngredients extends BottomSheetDialogFragment {
    RecyclerView rvIngredients;
    private static final String TAG = "Log Bottom Ingredients";

    public static BottomFragmentIngredients newInstance(Mill mill) {

        Bundle args = new Bundle();
        args.putSerializable("mill", mill);
        BottomFragmentIngredients fragment = new BottomFragmentIngredients();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addons_rv, container, false);
        rvIngredients = view.findViewById(R.id.rv_addons);
        Mill mill = (Mill) getArguments().getSerializable("mill");
        IngredientRecyclerAdapter adapter = new IngredientRecyclerAdapter(getActivity(),mill);

        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIngredients.setHasFixedSize(true);
        rvIngredients.setAdapter(adapter);
        return view;
    }



}

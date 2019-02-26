package com.sk.mysandwichv2.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.interfaces.MillListener;
import com.sk.mysandwichv2.mill.Mill;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;



public class OrderFragment extends Fragment implements MillListener {

    public final static String TAG = "Log OrderDetails Fragment";
    private Mill mill;
    private List<Mill> mills;
    private View view;
    private IntentFilter checkBoxChanged = new IntentFilter("CheckBoxChanged");
    private Intent updateCartBadge = new Intent("updateCartBadge");
    private Button btnSubmitOrder, btnEditMill, btnAddToCart;
    private TextView tvName, tvDescription, tvIngredientsList, tvDrinksList, tvIngredients, tvDrinks;
    private ImageView ivOrder;
    private TextView tvPrice;
    private int position;
//    private int orderPrice = 0;
    private String price;
    private Boolean fromCart;
    private Type millListTypeToken = new TypeToken<ArrayList<Mill>>() {
    }.getType();


    public static OrderFragment newInstance(Mill mill, Boolean fromCart, int position) {

        Bundle args = new Bundle();
        args.putSerializable("mill", mill);
        args.putBoolean("fromCart", fromCart);
        args.putInt("position", position);
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (getArguments() != null) {
            mill = (Mill) getArguments().getSerializable("mill");
            outState.putSerializable("mill", mill);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);
        setIds();
        fixBottomFrame();
        mill = (Mill) getArguments().getSerializable("mill");

        btnEditMill.setOnClickListener(btnEditMillListener);
        btnAddToCart.setOnClickListener(btnAddToCartListener);

        price = view.getResources().getString(R.string.price);


        addOnsListener();

        if (mill.getIngredients() == null) tvIngredients.setVisibility(View.GONE);
        if (mill.getDrinks() == null) tvDrinks.setVisibility(View.GONE);
        tvIngredients.setOnClickListener(v -> ingredientsListener());
        tvDrinks.setOnClickListener(v -> drinksListener());

        //TODO: check if necessary
//        update(mill);
        btnSubmitOrder.setOnClickListener(v -> transferToSummaryFragment());
        tvPrice.setText("מחיר - " + mill.getPrice()+"₪");
        tvName.setText(mill.getName());
        tvDescription.setText(mill.getDescription());
        Picasso.get().load(mill.getImg()).into(ivOrder);
        if (getSelectedIngredients().length() > 0) {
            tvIngredientsList.setText(getSelectedIngredients());
        } else {
            tvIngredientsList.setText("");
        }
        if (getSelectedDrinks().length() > 0) {
            tvDrinksList.setText(getSelectedDrinks());
        } else {
            tvDrinksList.setText("");
        }
//        tvPrice.setText(price + ": " + orderPrice);

        return view;
    }

    private void transferToSummaryFragment() {
        Intent intent = new Intent("OrderFragmentToSummaryFragment");
        intent.putExtra("mill", (Parcelable) mill);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);

    }

    private void ingredientsListener() {
        BottomFragmentIngredients bottomFragmentIngredients = BottomFragmentIngredients.newInstance(mill);
        bottomFragmentIngredients.show(getActivity().getSupportFragmentManager(), "Ingredient");
    }

    private void drinksListener() {
        BottomFragmentDrinks bottomFragmentDrinks = BottomFragmentDrinks.newInstance(mill);
        bottomFragmentDrinks.show(getActivity().getSupportFragmentManager(), "Drink");
    }


    private void addOnsListener() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Hello");
                if (getSelectedIngredients().length() > 0)
                    tvIngredientsList.setText(getSelectedIngredients());
                if (getSelectedDrinks().length() > 0) tvDrinksList.setText(getSelectedDrinks());
                tvPrice.setText("מחיר - " + mill.getPrice()+"₪");

            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, checkBoxChanged);
    }


    private void getSavedMill() {

        mills = getMillsFromPrefs();
        mills.add(mill);
        updateMillsToPrefs(mills);
    }

    private void updateMillsToPrefs(List<Mill> listFromPrefs) {
        Gson gson = new Gson();
        String jsonMill = gson.toJson(listFromPrefs, millListTypeToken);

        SharedPreferences pref = getActivity().getSharedPreferences("mill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("mill", jsonMill).apply();
    }

    private StringBuilder getSelectedIngredients() {
        StringBuilder builder = new StringBuilder();
        builder.append("תוספות: ");
        int count = 0;
        if (mill.getIngredients() != null) {
            for (int i = 0; i < mill.getIngredients().size(); i++) {
                if (mill.getIngredients().get(i).isSelected) {
                    count++;
                    String name = mill.getIngredients().get(i).getName();
                    builder.append(name + ", ");
                }
            }
        }
        if (count > 0) builder.replace(builder.length() - 2, builder.length() - 1, ".");
        if (count == 0){
            builder = new StringBuilder();
            builder.append("ללא תוספות");
        }

        return builder;
    }

    private StringBuilder getSelectedDrinks() {
        StringBuilder builder = new StringBuilder();
        builder.append("שתיה: ");
        int count = 0;
        if (mill.getDrinks() != null) {
            for (int i = 0; i < mill.getDrinks().size(); i++) {
                if (mill.getDrinks().get(i).isSelected()) {
                    count++;
                    String name = mill.getDrinks().get(i).getName();
                    builder.append(name + ", ");
                }
            }
        }
        if (count > 0) builder.replace(builder.length() - 2, builder.length() - 1, ".");
        if (count == 0){
            builder = new StringBuilder();
            builder.append("ללא שתיה");
        }
        return builder;
    }

    private List<Mill> getMillsFromPrefs() {
        Gson gson = new Gson();

        SharedPreferences pref = getActivity().getSharedPreferences("mill", Context.MODE_PRIVATE);
        List<Mill> mills;
        String jsonMill = pref.getString("mill", null);
        if (jsonMill == null) {
            mills = new ArrayList<>();
        } else {
            mills = gson.fromJson(jsonMill, millListTypeToken);
        }
        return mills;
    }

    View.OnClickListener btnEditMillListener = (v -> {
        position = getArguments().getInt("position");
        mills = getMillsFromPrefs();
        mills.set(position, mill);
        updateMillsToPrefs(mills);
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_container, new CartFragment()).commit();
//            getFragmentManager().beginTransaction().addToBackStack(null)
//                    .replace(R.id.main_container, new CartFragment(), "cartFragment").commit();

    });

    View.OnClickListener btnAddToCartListener = (v -> {
        getSavedMill();
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(updateCartBadge);
//        saveMill(mill);
//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new CartFragment()).commit();
    });

    private void setIds() {
        btnAddToCart = view.findViewById(R.id.btnAddToCart);
        btnSubmitOrder = view.findViewById(R.id.btnContinueToSummary);
        btnEditMill = view.findViewById(R.id.btnEditMill);
        ivOrder = view.findViewById(R.id.iv_img_order);
        tvName = view.findViewById(R.id.tv_name_order);
        tvDescription = view.findViewById(R.id.tv_description_order);
        tvIngredientsList = view.findViewById(R.id.tv_order_ingredients_list);
        tvDrinksList = view.findViewById(R.id.tv_order_drinks_list);
        tvIngredients = view.findViewById(R.id.ingredients);
        tvDrinks = view.findViewById(R.id.drinks);
        tvPrice = view.findViewById(R.id.tvPriceOrder);
    }

    private void fixBottomFrame() {
        if (getArguments() != null) {
            fromCart = getArguments().getBoolean("fromCart");
            if (fromCart) {
                btnAddToCart.setVisibility(View.GONE);
                btnSubmitOrder.setVisibility(View.GONE);
                btnEditMill.setVisibility(View.VISIBLE);
            } else {
                btnAddToCart.setVisibility(View.VISIBLE);
                btnSubmitOrder.setVisibility(View.VISIBLE);
                btnEditMill.setVisibility(View.GONE);

            }
        }

    }


    @Override
    public void update(Mill mill) {
        this.mill = mill;
    }

}

package com.sk.mysandwichv2.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.mill.Drinks;
import com.sk.mysandwichv2.mill.Mill;
import com.squareup.picasso.Picasso;


public class DrinkRecyclerAdapter extends RecyclerView.Adapter<DrinkRecyclerAdapter.ViewHolder> {

    private Activity activity;
    private Mill mill;
    private Intent checkBoxChanged = new Intent("CheckBoxChanged");

    public DrinkRecyclerAdapter(Activity activity, Mill mill) {
        this.activity = activity;
        this.mill = mill;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_ingredient_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Drinks drinks = mill.getDrinks().get(i);

        viewHolder.tvName.setText(drinks.getName());
        viewHolder.tvPrice.setText(String.valueOf(drinks.getPrice()));
        viewHolder.checkBox.setChecked(drinks.isSelected());
        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            drinks.setSelected(isChecked);
            if (isChecked) {
                mill.setPrice(mill.getPrice() + drinks.getPrice());
            }
            if (!isChecked) {
                mill.setPrice(mill.getPrice() - drinks.getPrice());
            }

            LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(checkBoxChanged));
        });

        if (!drinks.getImg().isEmpty()) {
            Picasso.get().load(drinks.getImg()).into(viewHolder.ivIngredient);
        }

    }


    @Override
    public int getItemCount() {
        return mill.getDrinks().size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvName, tvPrice;
        ImageView ivIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvPrice = itemView.findViewById(R.id.tv_ingredient_price);
            checkBox = itemView.findViewById(R.id.cb_ingredient);
            ivIngredient = itemView.findViewById(R.id.iv_Ingredient);

        }
    }
}

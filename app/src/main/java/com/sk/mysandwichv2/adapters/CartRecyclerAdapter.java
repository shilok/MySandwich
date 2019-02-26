package com.sk.mysandwichv2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.fragments.CartFragment;
import com.sk.mysandwichv2.mill.Mill;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartRecyclerAdapter extends RecyclerView.Adapter<CartRecyclerAdapter.ViewHolder> {
    private Intent millsCounterUpdate = new Intent("millsCounterUpdate");
    private Intent updateCartBadge = new Intent("updateCartBadge");
    private IntentFilter selectedMillsRemove = new IntentFilter("selectedMillsRemove");
    private Context context;
    private List<Mill> mills;
    private Type millListTypeToken = new TypeToken<ArrayList<Mill>>(){}.getType();

    private List<Mill> selectedMills = new ArrayList<>();
    private int millsPriceCounter = 0;
    private CartFragment cartFragment = new CartFragment();
    public boolean fromCode = false;

    public CartRecyclerAdapter(Context context, List<Mill> mills) {
        this.context = context;
        this.mills = mills;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_cart_view
                , viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        holder.mill = mills.get(i);

        holder.tvName.setText(holder.mill.getName());
        holder.tvPrice.setText("₪" + String.valueOf(holder.mill.getPrice()));
        fromCode = true;
        holder.checkBox.setChecked(holder.mill.isSelected());
        fromCode = false;

        if (holder.getSelectedIngredients().length() > 0) {
            holder.tvIngredients.setText(holder.getSelectedIngredients());
        } else {
            holder.tvIngredients.setText("");
        }
        if (holder.getSelectedDrinks().length() > 0) {
            holder.tvDrinks.setText(holder.getSelectedDrinks());
        } else {
            holder.tvDrinks.setText("");
        }
        Picasso.get().load(holder.mill.getImg()).into(holder.image);


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!fromCode) {
                holder.mill.setSelected(isChecked);

                if (isChecked) {
                    selectedMills.add(holder.mill);
//                    millsPriceCounter += holder.mill.getPrice();
                } else {
                    selectedMills.remove(holder.mill);
//                    millsPriceCounter -= holder.mill.getPrice();
                }
//                millsCounterUpdate.putExtra("millCounter", millsPriceCounter);
//                millsCounterUpdate.putExtra("selectedMills", (Serializable) selectedMills);
//                millsCounterUpdate.putExtra("mills", (Serializable) mills);
                millsCounterUpdate.putExtra("mill", (Serializable) holder.mill);
                LocalBroadcastManager.getInstance(context).sendBroadcast(millsCounterUpdate);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mills.size();
    }

    public void removeItem(int position) {
        mills.remove(position);
        notifyItemRemoved(position);

        updateMillsToPrefs(mills);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateCartBadge);


        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
    }

    public void restoreItem(Mill mill, int position) {
        mills.add(position, mill);
        notifyItemInserted(position);
        updateMillsToPrefs(mills);
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateCartBadge);

    }


    private void updateMillsToPrefs(List<Mill> listFromPrefs) {
        Gson gson = new Gson();
        String jsonMill = gson.toJson(listFromPrefs, millListTypeToken);

        SharedPreferences pref = context.getSharedPreferences("mill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("mill", jsonMill).apply();
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public int getItemViewType(int position) {
        return (position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Mill mill;
        private TextView tvName, tvIngredients, tvDrinks, tvPrice;
        private CheckBox checkBox;
        private ImageView image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_cart);
            tvIngredients = itemView.findViewById(R.id.tv_ingredients_cart);
            tvDrinks = itemView.findViewById(R.id.tv_drinks_cart);
            tvPrice = itemView.findViewById(R.id.tvPriceCart);
            image = itemView.findViewById(R.id.iv_img_cart);
            checkBox = itemView.findViewById(R.id.cbCart);


        }


        private StringBuilder getSelectedIngredients() {
            StringBuilder builder = new StringBuilder();
            builder.append("תוספות: ");
            int count = 0;
            for (int i = 0; i < mill.getIngredients().size(); i++) {
                if (mill.getIngredients().get(i).isSelected) {
                    count++;
                    String name = mill.getIngredients().get(i).getName();
                    builder.append(name + ", ");
                }
            }
            if (count > 0)
                builder.replace(builder.length() - 2, builder.length() - 1, ".");
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
            for (int i = 0; i < mill.getDrinks().size(); i++) {
                if (mill.getDrinks().get(i).isSelected()) {
                    count++;
                    String name = mill.getDrinks().get(i).getName();
                    builder.append(name + ", ");
                }
            }
            if (count > 0){
                builder.replace(builder.length() - 2, builder.length() - 1, ".");
            }
            if (count == 0){
                builder = new StringBuilder();
                builder.append("ללא שתיה");
            }
            return builder;
        }


    }


}

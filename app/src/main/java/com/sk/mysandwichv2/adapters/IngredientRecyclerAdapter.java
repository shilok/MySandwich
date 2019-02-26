package com.sk.mysandwichv2.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.mill.Ingredients;
import com.sk.mysandwichv2.mill.Mill;
import com.squareup.picasso.Picasso;


public class IngredientRecyclerAdapter extends RecyclerView.Adapter<IngredientRecyclerAdapter.ViewHolder> {

    private Intent checkBoxChanged = new Intent("CheckBoxChanged");
    private static final String TAG = "Log RecyclerAdapter";

    Activity activity;
    Mill mill;

    public IngredientRecyclerAdapter(Activity activity, Mill mill) {
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        Ingredients ingredients = mill.getIngredients().get(i);
//        Log.d(TAG, "onBindViewHolder: " + ingredients.toString());
        viewHolder.ingredients = mill.getIngredients().get(i);;

        viewHolder.tvName.setText(viewHolder.ingredients.getName());
        viewHolder.tvPrice.setText(String.valueOf(viewHolder.ingredients.getPrice()));
        viewHolder.fromCode = true;
        viewHolder.checkBox.setChecked(viewHolder.ingredients.getIsSelected());
        viewHolder.fromCode = false;

        if (viewHolder.ingredients.getImg().isEmpty()){
            Picasso.get().load(R.drawable.mcdonalds_logo).into(viewHolder.ivIngredient);

        }else {
            Picasso.get().load(viewHolder.ingredients.getImg()).into(viewHolder.ivIngredient);
        }


//
//        if (!viewHolder.ingredients.getImg().isEmpty()) {
//            Picasso.get().load(viewHolder.ingredients.getImg()).placeholder(R.drawable.ic_image_black).into(viewHolder.ivIngredient);
//
//        }

    }

    private void animateImage(ImageView imageView){
        imageView.setAnimation(new AlphaAnimation(500, 400));
    }


    @Override
    public int getItemCount() {
        return mill.getIngredients().size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        Ingredients ingredients;
        CheckBox checkBox;
        TextView tvName, tvPrice;
        ImageView ivIngredient;
        boolean fromCode = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvPrice = itemView.findViewById(R.id.tv_ingredient_price);
            checkBox = itemView.findViewById(R.id.cb_ingredient);
            ivIngredient = itemView.findViewById(R.id.iv_Ingredient);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ingredients.setSelected(isChecked);

                if (!fromCode && isChecked) {
                    mill.setPrice(mill.getPrice() + ingredients.getPrice());
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(checkBoxChanged));
                    animateImage(ivIngredient);
                }
                if (!fromCode && !isChecked){
                    mill.setPrice(mill.getPrice() - ingredients.getPrice());
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(checkBoxChanged));
                }

            });

        }
    }

}

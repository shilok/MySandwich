package com.sk.mysandwichv2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.mill.Mill;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SummaryRecyclerAdapter extends RecyclerView.Adapter<SummaryRecyclerAdapter.ViewHolder> {
    private List<Mill> mills;
    private Context context;

    public SummaryRecyclerAdapter(List<Mill> mills, Context context) {
        this.mills = mills;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_summary_order, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        holder.mill = mills.get(i);

        holder.tvName.setText(holder.mill.getName());
        holder.tvIngedients.setText(holder.getSelectedIngredients());
        holder.tvDrinks.setText(holder.getSelectedDrinks());

        if (holder.mill.getImg() != null) {
            Picasso.get().load(holder.mill.getImg()).into(holder.iv);
        }
        holder.tvPrice.setText("₪" + holder.mill.getPrice());

    }

    @Override
    public int getItemCount() {
        return mills.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Mill mill;
        private ImageView iv;
        private TextView tvName, tvIngedients, tvDrinks, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.ivSummary);
            tvName = itemView.findViewById(R.id.tvNameSummary);
            tvIngedients = itemView.findViewById(R.id.tvIngredientSummary);
            tvDrinks = itemView.findViewById(R.id.tvDrinksSummary);
            tvPrice = itemView.findViewById(R.id.tvPriceSummary);
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
    }
}

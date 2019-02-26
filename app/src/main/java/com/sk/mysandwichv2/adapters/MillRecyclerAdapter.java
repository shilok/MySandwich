package com.sk.mysandwichv2.adapters;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.transition.Fade;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionSet;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.fragments.OrderFragment;
import com.sk.mysandwichv2.mill.Mill;
import com.squareup.picasso.Picasso;

public class MillRecyclerAdapter extends FirebaseRecyclerAdapter<Mill, MillRecyclerAdapter.ViewHolder> {

    public final static String TAG = "Log Mill Adapter";

    FragmentActivity activity;



    public MillRecyclerAdapter(@NonNull FirebaseRecyclerOptions<Mill> options, FragmentActivity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Mill model) {
        holder.mill = model;
        String price = activity.getResources().getString(R.string.price);

        holder.tvName.setText(model.getName());
        holder.tvDescription.setText(model.getDescription());
        holder.tvPrice.setText(price + ": " +  model.getPrice());
        Picasso.get().load(model.getImg()).into(holder.image);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_mill_view, viewGroup, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        //Model in the viewHolder
        Mill mill;
        TextView tvName, tvPrice, tvDescription;
        ImageView image;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_mill_name);
            tvDescription = itemView.findViewById(R.id.tv_mill_description);
            tvPrice = itemView.findViewById(R.id.tv_mill_price);
            image = itemView.findViewById(R.id.iv_mill_img);

            itemView.setOnClickListener(v -> transferToOrderFragment());
        }

        private void transferToOrderFragment() {
            OrderFragment orderFragment = OrderFragment.newInstance(mill, false, 0);

            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

            Fade exitFade = new Fade();
            exitFade.setDuration(750);
//            categoryViewFragment.setExitTransition(exitFade);

            TransitionSet enterTransitionSet = new TransitionSet();
            enterTransitionSet.addTransition(TransitionInflater.from(activity).inflateTransition(android.R.transition.slide_right));
            enterTransitionSet.setDuration(600);
            enterTransitionSet.setStartDelay(150);
            orderFragment.setSharedElementEnterTransition(enterTransitionSet);


            Fade enterFade = new Fade();
            enterFade.setStartDelay(150);
            enterFade.setDuration(750);
            orderFragment.setEnterTransition(enterFade);
            orderFragment.setExitTransition(exitFade);


            View ivMill = itemView.findViewById(R.id.iv_mill_img);
            View tvMillName = itemView.findViewById(R.id.tv_mill_name);
            View tvMillDescription = itemView.findViewById(R.id.tv_mill_description);

            fragmentTransaction.addSharedElement(ivMill, ivMill.getTransitionName());
            fragmentTransaction.addSharedElement(tvMillName, tvMillName.getTransitionName());
            fragmentTransaction.addSharedElement(tvMillDescription, tvMillDescription.getTransitionName());

            fragmentTransaction.replace(R.id.main_container, orderFragment, "orderFragment").addToBackStack("");
            fragmentTransaction.commitAllowingStateLoss();


            Handler uiThread = new Handler();
            uiThread.postDelayed(() -> {
                orderFragment.setEnterTransition(null);
                orderFragment.setExitTransition(null);


            }, 1000);


        }
    }

}

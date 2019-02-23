package com.sk.mysandwichv2.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk.mysandwichv2.R;
import com.sk.mysandwichv2.adapters.CartRecyclerAdapter;
import com.sk.mysandwichv2.mill.Mill;
import com.sk.mysandwichv2.mill.OrderDetails;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CartFragment extends Fragment {
    private RecyclerView rv;
    private List<Mill> mills;
    private View view;
    private Paint p = new Paint();
    private CartRecyclerAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private Type millListTypeToken = new TypeToken<ArrayList<Mill>>() {
    }.getType();

    private Intent cartToOrder = new Intent("cartToOrder");
    private Intent updateCartBadge = new Intent("updateCartBadge");
    private Intent selectedMillsRemove = new Intent("selectedMillsRemove");
    private IntentFilter millsCounterUpdate = new IntentFilter("millsCounterUpdate");


    private List<Mill> selectedMills = new ArrayList<>();
    private boolean isAllSelected = false;
    private MenuItem cartBadge;
    private MenuItem removeItemFromCart, selectAllMills, unSelectAllMills;
    private TextView tvMillsCount;
    private int millsPriceCounter = 0;
    private int millsSelectedCounter = 0;
    private Button btnMakeOrder;
    private String totalPrice = "";
    private String makeOrder = "";
    private String cartTitle = "";


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);
        setHasOptionsMenu(true);

        coordinatorLayout = view.findViewById(R.id.cart_fragment);
        rv = view.findViewById(R.id.rv_cart);
        tvMillsCount = view.findViewById(R.id.tvMillsCount);
        btnMakeOrder = view.findViewById(R.id.btnMakeOrder);


        mills = getListFromPrefs();




        totalPrice = getString(R.string.totalPrice);
        makeOrder = getString(R.string.totalMills);
        cartTitle = getString(R.string.cartTitle);
        tvMillsCount.setText(totalPrice + ": " + millsPriceCounter + " ₪");
        btnMakeOrder.setText(makeOrder + "(" + millsSelectedCounter + ")" );
        getActivity().setTitle(cartTitle + "(" + mills.size() + ")");


        adapter = new CartRecyclerAdapter(getContext(), mills);
        adapter.onSelectedMillsRemovedListener();


        millsCounterListener();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        enableSwipe();

        btnMakeOrder.setOnClickListener(v -> btnMakeOrderListener());




        return view;
    }

    private void btnMakeOrderListener() {
        OrderDetails orderDetails = new OrderDetails(selectedMills);
        OrderSummaryFragment orderSummaryFragment = OrderSummaryFragment.newInstance(orderDetails);
        tvMillsCount.setText(totalPrice + ": " + millsPriceCounter + " ₪");
        btnMakeOrder.setText(makeOrder + "(" + millsSelectedCounter + ")" );

        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, orderSummaryFragment, "summaryFragment")
                .commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        removeItemFromCart = menu.findItem(R.id.removeFromCart);
        selectAllMills = menu.findItem(R.id.selectAll);
        unSelectAllMills = menu.findItem(R.id.unSelectAll);
        if (!removeItemFromCart.isVisible()) removeItemFromCart.setVisible(true);
        if (!isAllSelected) selectAllMills.setVisible(true);
        if (isAllSelected) unSelectAllMills.setVisible(true);

        cartBadge = menu.findItem(R.id.action_cart);
        cartBadge.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeFromCart:
                if (selectedMills == null || selectedMills.size() == 0) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "אנא סמן ארוחה להסרה", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                } else {
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(selectedMillsRemove);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(updateCartBadge);
                }
                return true;

            case R.id.selectAll:
               selectAllMills();
                return true;

            case R.id.unSelectAll:
                unSelectAllMills();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMillsToPrefs(List<Mill> listFromPrefs) {
        Gson gson = new Gson();
        String jsonMill = gson.toJson(listFromPrefs, millListTypeToken);

        SharedPreferences pref = getActivity().getSharedPreferences("mill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("mill", jsonMill).apply();
    }

    private void enableSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();


                if (direction == ItemTouchHelper.LEFT) {
                    Mill deletedModel = mills.get(position);
                    int deletedPosition = position;
                    adapter.removeItem(position);

                    Snackbar snackbar = Snackbar.make(coordinatorLayout, deletedModel.getName() + " הוסר", Snackbar.LENGTH_LONG);
                    snackbar.setAction("בטל", view -> adapter.restoreItem(deletedModel, deletedPosition));
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                } else {
                    Mill deletedModel = mills.get(position);
                    int deletedPosition = position;

                    cartToOrder.putExtra("mill", (Parcelable) mills.get(position));
                    cartToOrder.putExtra("position", position);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(cartToOrder);

                }


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {


                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF(
                                (float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,
                                (float) itemView.getBottom() - width);

                        c.drawBitmap(icon, null, icon_dest, p);

                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rv);
    }

    @Override
    public void onPause() {
        cartBadge.setVisible(true);
        millsPriceCounter = 0;
        millsSelectedCounter = 0;

        getActivity().setTitle("Mc");
        super.onPause();
    }

    private List<Mill> getListFromPrefs() {
        Gson gson = new Gson();
        SharedPreferences pref = getActivity().getSharedPreferences("mill", Context.MODE_PRIVATE);
        String jsonMill = pref.getString("mill", null);
        if (jsonMill == null) {
            mills = new ArrayList<>();
        } else {
            mills = gson.fromJson(jsonMill, millListTypeToken);
        }
        return mills;
    }

    private void selectAllMills(){
        millsPriceCounter = 0;
        selectedMills = new ArrayList<>();
        for (Mill mill : mills) {
            millsPriceCounter += mill.getPrice();
            mill.setSelected(true);
            selectedMills.add(mill);
        }


        millsSelectedCounter = selectedMills.size();
        tvMillsCount.setText(totalPrice + ": " + millsPriceCounter + " ₪");
        btnMakeOrder.setText(makeOrder + "(" + millsSelectedCounter + ")" );

        isAllSelected = true;

        adapter.notifyDataSetChanged();
        selectAllMills.setVisible(false);
        unSelectAllMills.setVisible(true);
    }

    private void unSelectAllMills(){
        for (Mill mill : mills) {
            mill.setSelected(false);
        }
        selectedMills = new ArrayList<>();
        millsSelectedCounter = 0;
        millsPriceCounter = 0;

        tvMillsCount.setText(totalPrice + ": " + millsPriceCounter + " ₪");
        btnMakeOrder.setText(makeOrder + "(" + millsSelectedCounter + ")" );


        adapter.notifyDataSetChanged();

    }

    private int getSelectedMills(List<Mill> mills){
        int result = 0;
        for (Mill mill : mills) {
            if (mill.isSelected()){
                result += mill.getPrice();
            }
        }
        return result;
    }

    private void millsCounterListener() {
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(millsCounterReceiver, millsCounterUpdate);
    }



    BroadcastReceiver millsCounterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            millsPriceCounter = intent.getIntExtra("millCounter", 0);
//            selectedMills = (List<Mill>) intent.getSerializableExtra("selectedMills");
//            mills = (List<Mill>) intent.getSerializableExtra("mills");


            Mill mill = (Mill) intent.getSerializableExtra("mill");




            if (mill.isSelected()){
                millsPriceCounter += mill.getPrice();
                selectedMills.add(mill);
            }else {
                millsPriceCounter -= mill.getPrice();
                selectedMills.remove(mill);
            }
            millsSelectedCounter = selectedMills.size();

            btnMakeOrder.setText(makeOrder + "(" + millsSelectedCounter + ")" );
            getActivity().setTitle(cartTitle + "(" + mills.size() + ")");
            tvMillsCount.setText(totalPrice + ": " + millsPriceCounter + " ₪");

            if (mills.size() == selectedMills.size()){
                selectAllMills.setVisible(false);
                unSelectAllMills.setVisible(true);
            }else {
                selectAllMills.setVisible(true);
                unSelectAllMills.setVisible(false);
            }

        }
    };

}

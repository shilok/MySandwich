package com.sk.mysandwichv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk.mysandwichv2.fragments.CartFragment;
import com.sk.mysandwichv2.fragments.CategoryViewFragment;
import com.sk.mysandwichv2.mill.Mill;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Log MainActivity";
    private static final int RC_SIGN_IN = 1;
    private CategoryViewFragment categoryViewFragment = new CategoryViewFragment();
    private CartFragment cartFragment = new CartFragment();
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private IntentFilter OnCallBackDone = new IntentFilter("OnCallBackDone");
    private IntentFilter updateCartBadge = new IntentFilter("updateCartBadge");
    private Type millListTypeToken = new TypeToken<ArrayList<Mill>>() {
    }.getType();


    private ActionBarDrawerToggle toggle;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private ImageView ivToolbar;
    private TextView tvCartCount;
    private String uid;

    private int cartCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setIds();


        listener.onAuthStateChanged(FirebaseAuth.getInstance());

        uid = FirebaseAuth.getInstance().getUid();


        setSupportActionBar(toolbar);
        updateBadgeCart();
        setUpdateCartBadgeListener();
        setDrawer();

        setListeners();
        onCallBackDoneReceiver();

        progressBar.setVisibility(View.VISIBLE);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, categoryViewFragment, "categoryFragment").commit();
    }

    private void appBarOffsetListener() {
        Handler handler = new Handler();
        handler.postDelayed(() -> ivToolbar.setVisibility(View.VISIBLE), 300);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            if (i < -166) {
                ivToolbar.animate().alpha(1.0f);
            } else {
                ivToolbar.animate().alpha(0.0f);
            }

        });
    }


    private void updateBadgeCart() {
        cartCount = getMillsFromPrefs().size();
    }


    private void moveToCartFragment() {
        if (!cartFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().addToBackStack(null)
                    .replace(R.id.main_container, cartFragment, "cartFragment")
                    .commit();
        }

    }


    View.OnClickListener toolbarListener = (v -> {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);
        }
    });


    @Override
    public void onBackPressed() {

        if (getFragmentByName() != null) {

            switch (getFragmentByName()) {
                case "orderFragment":


                    break;

                case "addressFragment":
                    CoordinatorLayout addressRecyclerLayout = findViewById(R.id.addressRecyclerLayout);
                    Snackbar snackbar = Snackbar.make(addressRecyclerLayout, "אנא בחר כתובת", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;

                case "categoryFragment":
                    break;

                case "summaryFragment":
                    break;

                case "cartFragment":

                    break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        }
        super.onBackPressed();

    }

    private String getFragmentByName() {
        return getSupportFragmentManager().findFragmentById(R.id.main_container).getTag();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        View cartView = menu.findItem(R.id.action_cart).getActionView();
        tvCartCount = cartView.findViewById(R.id.tv_cart_badge);
        FrameLayout flCart = cartView.findViewById(R.id.fl_shopping_cart);
        flCart.setOnClickListener(v -> moveToCartFragment());
        setupCartBadge();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item != null && item.getItemId() == android.R.id.home) {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.openDrawer(Gravity.RIGHT);
            }
        }

        return false;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_camera:
                Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_gallery:
                Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show();
                break;
        }
        try {
            drawer.closeDrawer(GravityCompat.START);

        } catch (Exception e) {
            drawer.closeDrawer(GravityCompat.END);

        }
        return true;
    }


    private void onCallBackDoneReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressBar.setVisibility(View.GONE);
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, OnCallBackDone);
    }


    private void setIds() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        appBarLayout = findViewById(R.id.appbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ivToolbar = findViewById(R.id.iv_toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);

    }

    private void setListeners() {
        toolbar.setNavigationOnClickListener(toolbarListener);
        navigationView.setNavigationItemSelectedListener(this);
        appBarOffsetListener();

    }

    private void setupCartBadge() {

        if (tvCartCount != null) {
            if (cartCount == 0) {
                if (tvCartCount.getVisibility() != View.GONE) {
                    tvCartCount.setVisibility(View.GONE);
                }
            } else {
                tvCartCount.setText(String.valueOf(cartCount));
                if (tvCartCount.getVisibility() != View.VISIBLE) {
                    tvCartCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setUpdateCartBadgeListener() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                cartCount = getMillsFromPrefs().size();
                setupCartBadge();
                invalidateOptionsMenu();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, updateCartBadge);
    }




    private List<Mill> getMillsFromPrefs() {
        Gson gson = new Gson();

        SharedPreferences pref = getSharedPreferences("mill", Context.MODE_PRIVATE);
        List<Mill> mills;
        String jsonMill = pref.getString("mill", null);
        if (jsonMill == null) {
            mills = new ArrayList<>();
        } else {
            mills = gson.fromJson(jsonMill, millListTypeToken);
        }
        return mills;
    }

    private void setDrawer() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    private FirebaseAuth.AuthStateListener listener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.AnonymousBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    };
}


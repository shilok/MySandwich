package com.sk.mysandwichv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.transition.Fade;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionSet;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sk.mysandwichv2.fragments.CartFragment;
import com.sk.mysandwichv2.fragments.CategoryViewFragment;
import com.sk.mysandwichv2.fragments.OrderFragment;
import com.sk.mysandwichv2.mill.Mill;
import com.sk.mysandwichv2.mill.ShippingAddress;

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
    private IntentFilter transitionRvFragmentToOrderFragment = new IntentFilter("rvFragmentToOrderFragment");
    private IntentFilter transitionOrderFragmentToSummaryFragment = new IntentFilter("OrderFragmentToSummaryFragment");
    private IntentFilter OnCallBackDone = new IntentFilter("OnCallBackDone");
    private IntentFilter updateCartBadge = new IntentFilter("updateCartBadge");
    private IntentFilter cartToOrder = new IntentFilter("cartToOrder");
    private IntentFilter enterToCartFragment = new IntentFilter("enterToCartFragment");
    private IntentFilter enterToViewFragment = new IntentFilter("enterToViewFragment");
    private IntentFilter exitFromCartFragment = new IntentFilter("exitFromCartFragment");
    private IntentFilter exitFromViewFragment = new IntentFilter("exitFromViewFragment");
    private Type millListTypeToken = new TypeToken<ArrayList<Mill>>() {
    }.getType();


    private Button btnEditMill, btnAddToCart, btnContinueToSummary;
    private ActionBarDrawerToggle toggle;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private Handler uiThread;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private ImageView ivToolbar, ivCollapseToolbar;
    private MenuItem cartIconMenuItem;
    private TextView tvCartCount;
    private ImageButton ivCart;
    private FirebaseUser firebaseUser;
    private String uid;

    private int cartCount = 0;
    private boolean appBarExpanded;


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
        onCallBackDoneReciever();

        progressBar.setVisibility(View.VISIBLE);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, categoryViewFragment, "categoryFragment").commit();
    }

    private void appBarOffsetListener() {
        Handler handler = new Handler();
        handler.postDelayed(() -> ivToolbar.setVisibility(View.VISIBLE), 300);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, i) -> {
            appBarExpanded = (i == 0);

            if (i < -166) {
                System.out.println("if (i < -166) ");
                ivToolbar.animate().alpha(1.0f);
            } else {
                System.out.println("else");
                ivToolbar.animate().alpha(0.0f);
            }

        });
    }


    private void updateBadgeCart() {
        cartCount = getMillsFromPrefs().size();
    }

    private void CartToOrderFragmentListener() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isDestroyed()) return;

                Parcelable mill = intent.getParcelableExtra("mill");
                int position = intent.getIntExtra("position", 0);

                OrderFragment orderFragment = OrderFragment.newInstance((Mill) mill, true, position);
                getSupportFragmentManager().beginTransaction().addToBackStack(null)
                        .replace(R.id.main_container, orderFragment, "orderFragment")
                        .commit();


//                Fade exitFade = new Fade();
//                exitFade.setDuration(750);
//                cartFragment.setExitTransition(exitFade);
//
//                TransitionSet enterTransitionSet = new TransitionSet();
//                enterTransitionSet.addTransition(TransitionInflater.from(MainActivity.this).inflateTransition(android.R.transition.slide_right));
//                enterTransitionSet.setDuration(600);
//                enterTransitionSet.setStartDelay(150);
//                orderFragment.setSharedElementEnterTransition(enterTransitionSet);
//
//
//                Fade enterFade = new Fade();
//                enterFade.setStartDelay(150);
//                enterFade.setDuration(750);
//                orderFragment.setEnterTransition(enterFade);
//                orderFragment.setExitTransition(exitFade);


//                View ivMill = findViewById(R.id.iv_mill_img);
//                View tvMillName = findViewById(R.id.tv_mill_name);
//                View tvMillDescription = findViewById(R.id.tv_mill_description);
//
//                fragmentTransaction.addSharedElement(ivMill, ivMill.getTransitionName());
//                fragmentTransaction.addSharedElement(tvMillName, tvMillName.getTransitionName());
//                fragmentTransaction.addSharedElement(tvMillDescription, tvMillDescription.getTransitionName());

//                fragmentTransaction.replace(R.id.container2, orderFragment).addToBackStack("");
//                fragmentTransaction.replace(R.id.main_container, orderFragment, "orderFragment").addToBackStack("");
//                fragmentTransaction.commitAllowingStateLoss();


//                uiThread = new Handler();
//                uiThread.postDelayed(() -> {
//                    orderFragment.setEnterTransition(null);
//                    orderFragment.setExitTransition(null);
//
//                }, 1000);

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, cartToOrder);

    }


    private void RvFragmentToOrderFragmentListener() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isDestroyed()) return;
//                Parcelable mill = intent.getParcelableExtra("mill");
//
//                OrderFragment orderFragment = OrderFragment.newInstance((Mill) mill, false, 0);
//
//
//                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//
//                Fade exitFade = new Fade();
//                exitFade.setDuration(750);
//                categoryViewFragment.setExitTransition(exitFade);
//
//                TransitionSet enterTransitionSet = new TransitionSet();
//                enterTransitionSet.addTransition(TransitionInflater.from(MainActivity.this).inflateTransition(android.R.transition.slide_right));
//                enterTransitionSet.setDuration(600);
//                enterTransitionSet.setStartDelay(150);
//                orderFragment.setSharedElementEnterTransition(enterTransitionSet);
//
//
//                Fade enterFade = new Fade();
//                enterFade.setStartDelay(150);
//                enterFade.setDuration(750);
//                orderFragment.setEnterTransition(enterFade);
//                orderFragment.setExitTransition(exitFade);
//
//
//                View ivMill = findViewById(R.id.iv_mill_img);
//                View tvMillName = findViewById(R.id.tv_mill_name);
//                View tvMillDescription = findViewById(R.id.tv_mill_description);
//
//                fragmentTransaction.addSharedElement(ivMill, ivMill.getTransitionName());
//                fragmentTransaction.addSharedElement(tvMillName, tvMillName.getTransitionName());
//                fragmentTransaction.addSharedElement(tvMillDescription, tvMillDescription.getTransitionName());
//
//                fragmentTransaction.replace(R.id.main_container, orderFragment, "orderFragment").addToBackStack("");
//                fragmentTransaction.commitAllowingStateLoss();
//
//
//                uiThread = new Handler();
//                uiThread.postDelayed(() -> {
//                    orderFragment.setEnterTransition(null);
//                    orderFragment.setExitTransition(null);
//
//
//                }, 1000);

            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, transitionRvFragmentToOrderFragment);

    }

//    private void OrderFragmentToSummaryFragmentListener() {
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (isDestroyed()) return;
//                Parcelable mill = intent.getParcelableExtra("mill");
//
//                OrderFragment orderFragment = new OrderFragment();
//                OrderSummaryFragment summaryFragment = OrderSummaryFragment.newInstance((Mill) mill, false);
//
//                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//
//                Fade exitFade = new Fade();
//                exitFade.setDuration(750);
//                orderFragment.setExitTransition(exitFade);
//
//                TransitionSet enterTransitionSet = new TransitionSet();
//                enterTransitionSet.addTransition(TransitionInflater.from(MainActivity.this).inflateTransition(android.R.transition.slide_top));
//                enterTransitionSet.setDuration(600);
//                enterTransitionSet.setStartDelay(150);
//                summaryFragment.setSharedElementEnterTransition(enterTransitionSet);
//
//
//                Fade enterFade = new Fade();
//                enterFade.setStartDelay(150);
//                enterFade.setDuration(750);
//                summaryFragment.setEnterTransition(enterFade);
//                summaryFragment.setExitTransition(exitFade);
//
//                fragmentTransaction.replace(R.id.main_container, summaryFragment, "summaryFragment").addToBackStack("");
//                fragmentTransaction.commitAllowingStateLoss();
//
//
//            }
//        };
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, transitionOrderFragmentToSummaryFragment);
//
//    }

    private void moveToCartFragment() {
        if (!cartFragment.isVisible()) {
            mFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.main_container, cartFragment, "cartFragment").commit();
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
        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

        System.out.println(getFragmentByName());
        if (getFragmentByName() != null) {

            switch (getFragmentByName()) {
                case "orderFragment":


                    break;

                case "fragmentAddress":
                    CoordinatorLayout summaryLayout = findViewById(R.id.summaryLayout);
                    if (summaryLayout.getVisibility() == View.GONE) {
                        summaryLayout.setVisibility(View.VISIBLE);
                    }
                    break;

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
        } else {
            super.onBackPressed();
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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


    private void onCallBackDoneReciever() {
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
        ivCollapseToolbar = findViewById(R.id.ivCollapseToolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        btnEditMill = findViewById(R.id.btnEditMill);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnContinueToSummary = findViewById(R.id.btnContinueToSummary);

    }

    private void setListeners() {
        toolbar.setNavigationOnClickListener(toolbarListener);
        navigationView.setNavigationItemSelectedListener(this);
        CartToOrderFragmentListener();
        appBarOffsetListener();

//        setEnterToCartFragmentListener();
//        setExitFromCartFragmentListener();
        setEnterToViewFragmentListener();
        setExitFromViewFragmentListener();
//        RvFragmentToOrderFragmentListener();
//        OrderFragmentToSummaryFragmentListener();
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


    public void setEnterToViewFragmentListener() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ivCollapseToolbar.setVisibility(View.VISIBLE);
            }
        }, enterToViewFragment);
    }

    public void setExitFromViewFragmentListener() {
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ivCollapseToolbar.setVisibility(View.GONE);
            }
        }, exitFromViewFragment);
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

    private void saveAddress() {
//        ShippingAddress shippingAddress = new ShippingAddress("shilo kohelet", "harashash 25", "Rosh HaAyin", "0545552425", false);
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("addresses").child(uid);
//        ref.setValue(shippingAddress);

        List<ShippingAddress> list = new ArrayList<>();
        list.add(new ShippingAddress("a", "D", "d", "d", false));
        list.add(new ShippingAddress("b", "D", "d", "d", false));
        list.add(new ShippingAddress("c", "D", "d", "d", false));
        list.add(new ShippingAddress("E", "D", "d", "d", false));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("addresses").child(uid);
        ref.setValue(list);
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


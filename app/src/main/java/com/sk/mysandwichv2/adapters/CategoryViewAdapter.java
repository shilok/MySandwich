package com.sk.mysandwichv2.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;

import com.sk.mysandwichv2.fragments.RvFragment;
import com.sk.mysandwichv2.mill.Category;

import java.util.List;

public class CategoryViewAdapter extends FragmentStatePagerAdapter {

    public final static String TAG = "Log Fragment Adapter";
    Intent intent = new Intent("OnCallBackDone");

    private List<Category> list;

    public CategoryViewAdapter(FragmentManager fm, List<Category> list) {
        super(fm);
        this.list = list;

    }

    @Override
    public Fragment getItem(int i) {
        RvFragment rvFragment = RvFragment.newInstance(list.get(i).getName());
        Activity activity = new Activity();
        if (i == 3){
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
        }
        return rvFragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).getName();
    }
}

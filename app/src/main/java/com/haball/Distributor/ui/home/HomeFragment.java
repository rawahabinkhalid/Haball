package com.haball.Distributor.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.haball.Distributor.DistributorDashboard;
import com.haball.Distributor.ui.main.SectionsPagerAdapter;
import com.haball.R;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private int myVal = -1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences selectedTab = getContext().getSharedPreferences("OrderTabsFromDraft",
                Context.MODE_PRIVATE);
        int value = Integer.parseInt(selectedTab.getString("TabNo", "0"));
        if (myVal == -1)
            myVal = value;
        Log.i("OrderTabsFromDraft", String.valueOf(myVal));

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getActivity(), getChildFragmentManager());
        final ViewPager viewPager = root.findViewById(R.id.view_pager1);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(sectionsPagerAdapter);
        if (myVal != -1)
            viewPager.setCurrentItem(myVal);
        TabLayout tabs = root.findViewById(R.id.tabs1);
        tabs.setupWithViewPager(viewPager);

        return root;
    }

}
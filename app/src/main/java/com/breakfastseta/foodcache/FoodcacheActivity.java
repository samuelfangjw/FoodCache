package com.breakfastseta.foodcache;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FoodcacheActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    String[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodcache);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        tabs = getResources().getStringArray(R.array.tabs);

        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabs[position]);
                    }
                }).attach();
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }
}
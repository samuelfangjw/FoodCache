package com.breakfastseta.foodcache;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

        FloatingActionButton buttonAddItem = findViewById(R.id.add_item_fab);
        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FoodcacheActivity.this, AddIngredientActivity.class));
            }
        });

        tabs = getResources().getStringArray(R.array.tabs);

        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabs[position]);
                    }
                }).attach();
    }

    // Use custom menu as menu for this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.inventory_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Decide what to do when menu item clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                startActivity(new Intent(FoodcacheActivity.this, ProfileActivity.class));
                return true;
            case R.id.shopping_list:
                startActivity(new Intent(FoodcacheActivity.this, ShoppingListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }
}
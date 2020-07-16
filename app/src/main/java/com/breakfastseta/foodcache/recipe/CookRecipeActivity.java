package com.breakfastseta.foodcache.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class CookRecipeActivity extends AppCompatActivity {

    ArrayList<String> steps;
    ArrayList<Map<String, Object>> ingredients;

    CookAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_recipe);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        steps = (ArrayList<String>) bundle.getSerializable("steps");
        ingredients = (ArrayList<Map<String, Object>>) bundle.getSerializable("ingredients");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cook Recipe!");

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new CookAdapter(steps);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void finishCooking(View view) {
        Intent intent = new Intent(CookRecipeActivity.this, RemoveQuantityActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ingredients",(Serializable) ingredients);
        intent.putExtra("bundle", args);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
package com.breakfastseta.foodcache.recipe;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.breakfastseta.foodcache.R;

import java.util.ArrayList;

public class AddRecipeActivity extends AppCompatActivity {

    // Recipe fields
    Uri image_path = null;
    String name;
    String author;
    ArrayList<Ingredient> ingredients;
    String steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
    }
}
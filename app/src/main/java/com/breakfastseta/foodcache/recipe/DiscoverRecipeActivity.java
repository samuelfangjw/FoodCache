package com.breakfastseta.foodcache.recipe;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DiscoverRecipeActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverRecipeActivity";

    DiscoverRecipeAdapter adapter;
    ArrayList<Recipe> arr = new ArrayList<>();

    RecyclerView recyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recipeRef = db.collection("Recipes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_recipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Discover");
        Util.createToolbar(this, toolbar);

        recyclerView = findViewById(R.id.recycler_view);

        getData();


    }

    private void getData() {
        recipeRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot snapshots = task.getResult();
                    createRecyclerView(snapshots);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void createRecyclerView(QuerySnapshot snapshots) {
        for (DocumentSnapshot document : snapshots) {
            String imagePath = document.getString("photo");
            String author = document.getString("author");
            String name = document.getString("name");
            String cuisine = document.getString("cuisine");
            ArrayList<Map<String, Object>> ingredients = (ArrayList<Map<String, Object>>) document.get("ingredients");
            ArrayList<String> steps = (ArrayList<String>) document.get("steps");
            ArrayList<Ingredient> ingredientsArr = new ArrayList<>();

            for (Map<String, Object> i : ingredients) {
                String ingredient_name = (String) i.get("name");
                String ingredient_units = (String) i.get("units");
                double ingredient_quantity = (double) i.get("quantity");
                ingredientsArr.add(new Ingredient(ingredient_name, ingredient_quantity, ingredient_units));
            }
            arr.add(new Recipe(name, author, ingredientsArr, steps, imagePath, cuisine));
        }

        // Create adapter passing in the sample user data
        adapter = new DiscoverRecipeAdapter(arr);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
package com.breakfastseta.foodcache.recipe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

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

public class DiscoverRecipeActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverRecipeActivity";

    DiscoverRecipeAdapter adapter;
    ArrayList<DiscoverSnippet> arr = new ArrayList<>();

    RecyclerView recyclerView;
    EditText etSearch;

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

        etSearch = findViewById(R.id.edit_text_search);
        recyclerView = findViewById(R.id.recycler_view);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        getData();


    }

    private void search(String text) {
        if (text.trim().isEmpty()) {
            adapter.filterList(arr);
        } else {
            ArrayList<DiscoverSnippet> filteredArr = new ArrayList<>();
            for (DiscoverSnippet snippet : arr) {
                if (snippet.getName().toLowerCase().contains(text.trim().toLowerCase())) {
                    filteredArr.add(snippet);
                }
            }
            adapter.filterList(filteredArr);
        }
    }

    private void getData() {
        recipeRef.whereEqualTo("isPublic", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
            String name = document.getString("name");
            String cuisine = document.getString("cuisine");
            String path = document.getReference().getPath();

            arr.add(new DiscoverSnippet(name, imagePath, path, cuisine));
        }

        // Create adapter passing in the sample user data
        adapter = new DiscoverRecipeAdapter(arr);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
package com.breakfastseta.foodcache.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.recipe.ViewRecipeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

public class DiscoverRecipeActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverRecipeActivity";

    DiscoverRecipeAdapter adapter;
    ArrayList<DiscoverSnippet> arr = new ArrayList<>();
    ArrayList<DiscoverSnippet> filteredArr = new ArrayList<>();
    ArrayList<DiscoverSnippet> searchArr = new ArrayList<>();
    ArrayList<String> cuisines;

    ArrayAdapter<String> arrayAdapter;

    RecyclerView recyclerView;
    EditText etSearch;
    Spinner spinner;

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
        spinner = findViewById(R.id.spinner_cuisine);

        cuisines = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cuisines)));
        cuisines.add(0, "All");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cuisines) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                view.setVisibility(View.GONE);
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

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

    private void setSpinnerListener() {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter(cuisines.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filter(String s) {
        filteredArr = new ArrayList<>();
        if (s.equals("All")) {
            filteredArr.addAll(arr);
        } else {
            for (DiscoverSnippet snippet : arr) {
                if (snippet.getCuisine().equals(s)) {
                    filteredArr.add(snippet);
                }
            }
        }

        search(etSearch.getText().toString());
    }

    private void search(String text) {
        searchArr = new ArrayList<>();
        if (text.trim().isEmpty()) {
            searchArr.addAll(filteredArr);
        } else {
            for (DiscoverSnippet snippet : filteredArr) {
                if (snippet.getName().toLowerCase().contains(text.trim().toLowerCase())) {
                    searchArr.add(snippet);
                }
            }
        }
        adapter.filterList(searchArr);
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

        filteredArr.addAll(arr);
        searchArr.addAll(arr);

        // Create adapter passing in the sample user data
        adapter = new DiscoverRecipeAdapter(searchArr);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setSpinnerListener();

        adapter.setOnItemClickListener(new DiscoverRecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String path, int position) {
                Intent intent = new Intent(DiscoverRecipeActivity.this, ViewRecipeActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
    }

}
package com.breakfastseta.foodcache.recipe.viewrecipe;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RemoveQuantityActivity extends AppCompatActivity {
    private static final String TAG = "RemoveQuantityActivity";

    ArrayList<Map<String, Object>> ingredients;
    HashMap<String, ExistingIngredientSnippet> ingredientsOwned = new HashMap<>();
    Map<String, ExistingIngredientSnippet> ingredientsMatch = new HashMap<>();
    ArrayList<String> keys = new ArrayList<>();

    RecyclerView recyclerView;
    RemoveQuantityAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryRef = db.collection("Users").document(App.getFamilyUID()).collection("Inventory");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_quantity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Ingredients Used");

        Bundle bundle = getIntent().getBundleExtra("bundle");
        ingredients = (ArrayList<Map<String, Object>>) bundle.getSerializable("ingredients");

        recyclerView = findViewById(R.id.recycler_view);

        getIngredientsOwned();
    }

    // get list of all ingredients owned. if same ingredient in multiple locations, combine them tgt
    private void getIngredientsOwned() {
        inventoryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot snapshots = task.getResult();

                for (DocumentSnapshot document : snapshots) {
                    String name = document.getString("ingredient");
                    String units = document.getString("units");
                    String mapKey = name + units;

                    Double quantity = document.getDouble("quantity");
                    String location = document.getString("location");
                    String path = document.getReference().getPath();

                    if (ingredientsOwned.containsKey(mapKey)) {
                        ingredientsOwned.get(mapKey).newLocation(location, quantity, path);
                    } else {
                        ingredientsOwned.put(mapKey, new ExistingIngredientSnippet(name, quantity, units, path, location));
                    }

                }
                checkChanges();
            }
        });
    }

    // Check which ingredients can be deleted (same name and units)
    private void checkChanges() {
        for (Map<String, Object> i : ingredients) {
            String name = (String) i.get("name");
            double quantity = (double) i.get("quantity");
            String units = (String) i.get("units");
            String key = name + units;

            if (ingredientsOwned.containsKey(key)) {
                ExistingIngredientSnippet existingSnippet = ingredientsOwned.get(key);
                existingSnippet.setQuantityUsed(quantity);
                ingredientsMatch.put(existingSnippet.getName(), existingSnippet);
            }
        }
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        keys.addAll(ingredientsMatch.keySet());

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new RemoveQuantityAdapter(keys, ingredientsMatch, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void acceptChanges(View view) {
        //TODO FINISH THIS
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

    class ExistingIngredientSnippet {
        private Double quantityUsed;
        private String name;
        private Double quantity;
        private String units;
        private Map<String, String> pathMap = new HashMap<>();
        private Map<String, Double> quantityMap = new HashMap<>();

        ExistingIngredientSnippet(String name, Double quantity, String units, String path, String location) {
            this.name = name;
            this.quantity = quantity;
            this.units = units;
            pathMap.put(location, path);
            quantityMap.put(location, quantity);
        }

        public String getName() {
            return name;
        }

        public Double getQuantity() {
            return quantity;
        }

        public String getUnits() {
            return units;
        }

        public Map<String, String> getPathMap() {
            return pathMap;
        }

        public Map<String, Double> getQuantityMap() {
            return quantityMap;
        }

        public void newLocation(String location, Double quantity, String path) {
            quantity += quantity;
            quantityMap.put(location, quantity);
            pathMap.put(location, path);
        }

        public void setQuantityUsed(Double quantityUsed) {
            this.quantityUsed = quantityUsed;
        }

        public Double getQuantityUsed() {
            return quantityUsed;
        }
    }
}
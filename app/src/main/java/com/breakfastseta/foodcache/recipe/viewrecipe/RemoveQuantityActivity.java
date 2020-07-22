package com.breakfastseta.foodcache.recipe.viewrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.profile.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RemoveQuantityActivity extends AppCompatActivity {
    private static final String TAG = "RemoveQuantityActivity";

    ArrayList<Map<String, Object>> ingredients;
    HashMap<String, ExistingIngredientSnippet> ingredientsOwned = new HashMap<>();
    Map<String, ExistingIngredientSnippet> ingredientsMatch = new HashMap<>();
    ArrayList<String> keys = new ArrayList<>();
    String path;

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
        path = bundle.getString("path");

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
                    String nameLowerCase = document.getString("nameLowerCase");
                    String mapKey = nameLowerCase + units;
                    Double quantity = document.getDouble("quantity");
                    String location = document.getString("location");
                    String path = document.getReference().getPath();
                    Map<String, Double> expiryMap = (Map<String, Double>) document.get("expiryMap");

                    if (ingredientsOwned.containsKey(mapKey)) {
                        ingredientsOwned.get(mapKey).newLocation(location, quantity, path, expiryMap);
                    } else {
                        ingredientsOwned.put(mapKey, new ExistingIngredientSnippet(name, quantity, units, path, location, expiryMap));
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

            String key = name.toLowerCase() + units;

            if (ingredientsOwned.containsKey(key)) {
                ExistingIngredientSnippet existingSnippet = ingredientsOwned.get(key);
                existingSnippet.setQuantityUsed(quantity);
                ingredientsMatch.put(name, existingSnippet);
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

        if (keys.isEmpty()) {
            findViewById(R.id.message).setVisibility(View.VISIBLE);
        }
    }

    public void acceptChanges(View view) {
        Map<String, ExistingIngredientSnippet> map;
        if (adapter.map == null) {
            map = new HashMap<>();
        } else {
            map = adapter.map;
        }

        for (ExistingIngredientSnippet snip : map.values()) {
            Map<String, Map<String, Double>> expiryMapMap = snip.getExpiryMap();
            Map<String, String> pathMap = snip.getPathMap();
            Map<String, Double> quantityMap = snip.getQuantityMap();

            for (String location : pathMap.keySet()) {
                Map<String, Double> expiryMap = expiryMapMap.get(location);
                String path = pathMap.get(location);
                Double quantityLeft = quantityMap.get(location);

                if (quantityLeft == 0.0) {
                    db.document(path).delete();
                } else {
                    Map<String, Object> update = new HashMap<String, Object>();
                    update.put("quantity", quantityLeft);

                    TreeMap<Timestamp, Double> tree = new TreeMap<>(Collections.reverseOrder());
                    for (String key : expiryMap.keySet()) {
                        Timestamp ts = Inventory.stringToTimestamp(key);
                        tree.put(ts, expiryMap.get(key));
                    }

                    Map<String, Double> newExpiryMap = new HashMap<>();
                    Timestamp dateTimestamp = null;
                    for (Map.Entry<Timestamp, Double> entry : tree.entrySet()) {
                        if (quantityLeft > 0) {
                            Timestamp ts = entry.getKey();
                            Double quantity = entry.getValue();
                            dateTimestamp = ts;

                            if (quantity >= quantityLeft) {
                                quantity = quantityLeft;
                            }
                            quantityLeft -= quantity;
                            newExpiryMap.put(ts.toString(), quantity);
                        }
                    }

                    update.put("dateTimestamp", dateTimestamp);
                    update.put("expiryMap", newExpiryMap);
                    db.document(path).update(update);

                    //TODO fix this shit
                    Log.d(TAG, "acceptChanges: " + path);
                    Log.d(TAG, "acceptChanges: " + update);
                }
            }

        }

        Profile profile = App.getProfile();
        profile.addRecipePrepared(path);
        App.getProfileRef().update("recipesPrepared", profile.getRecipesPrepared());

        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra("path", path);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    class ExistingIngredientSnippet {
        private Double quantityUsed;
        private String name;
        private Double quantity;
        private String units;
        private Map<String, String> pathMap = new HashMap<>();
        private Map<String, Double> quantityMap = new HashMap<>();
        private Map<String, Map<String, Double>> expiryMap = new HashMap<>();

        ExistingIngredientSnippet(String name, Double quantity, String units, String path, String location, Map<String, Double> expiryMap) {
            this.name = name;
            this.quantity = quantity;
            this.units = units;
            pathMap.put(location, path);
            quantityMap.put(location, quantity);
            this.expiryMap.put(location, expiryMap);
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

        public void newLocation(String location, Double quantity, String path, Map<String, Double> expiryMap) {
            this.quantity += quantity;
            this.quantityMap.put(location, quantity);
            this.pathMap.put(location, path);
            this.expiryMap.put(location, expiryMap);
        }

        public void setQuantityUsed(Double quantityUsed) {
            this.quantityUsed = quantityUsed;
        }

        public Double getQuantityUsed() {
            return quantityUsed;
        }

        public Map<String, Map<String, Double>> getExpiryMap() {
            return expiryMap;
        }
    }
}
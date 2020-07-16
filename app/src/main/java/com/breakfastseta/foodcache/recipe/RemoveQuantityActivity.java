package com.breakfastseta.foodcache.recipe;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ArrayList<CompareSnippet> ingredientsMatch = new ArrayList<>();
    Map<String, Double> noMatch = new HashMap<>();

    TableLayout table1;
    TableLayout table2;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");

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

        table1 = findViewById(R.id.table_layout_matches);
        table2 = findViewById(R.id.table_layout_no_matches);

        getIngredientsOwned();
    }

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

    private void checkChanges() {
        for (Map<String, Object> i : ingredients) {
            String name = (String) i.get("name");
            double quantity = (double) i.get("quantity");
            String units = (String) i.get("units");
            String key = name + units;

            if (ingredientsOwned.containsKey(key)) {
                ExistingIngredientSnippet existingSnippet = ingredientsOwned.get(key);
                ingredientsMatch.add(new CompareSnippet(quantity, existingSnippet));
            } else {
                noMatch.put(name, quantity);
            }
        }
        displayChanges();
    }

    private void displayChanges() {
        
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(8,8,8,8);

        for (CompareSnippet cs : ingredientsMatch){
            TableRow row1 = new TableRow(this);
            TextView tv = new TextView(this);
            row1.addView(tv);

            ExistingIngredientSnippet snippet = cs.getSnippet();

            tv.setText(snippet.getName());

            TableRow row2 = new TableRow(this);
            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            ImageView iv = new ImageView(this);

            tv.setLayoutParams(params);
            iv.setLayoutParams(params);
            tv1.setLayoutParams(params);
            tv2.setLayoutParams(params);

            //TODO need to consider multiple items stored in diff locations
            double quantityUsed = cs.getQuantityUsed();
            double quantityBefore = snippet.getQuantity();
            String units = snippet.getUnits();

            tv1.setText(Util.formatQuantity(quantityBefore, units));
            tv2.setText(Util.formatQuantity(quantityBefore - quantityUsed, units));
            iv.setImageDrawable(getDrawable(R.drawable.ic_arrow_right));
            row2.addView(tv1);
            row2.addView(iv);
            row2.addView(tv2);

            table1.addView(row1);
            table1.addView(row2);
        }

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

    class CompareSnippet {
        private double quantityUsed;
        private ExistingIngredientSnippet snippet;

        CompareSnippet(double quantityUsed, ExistingIngredientSnippet snippet) {
            this.quantityUsed = quantityUsed;
            this.snippet = snippet;
        }

        public double getQuantityUsed() {
            return quantityUsed;
        }

        public ExistingIngredientSnippet getSnippet() {
            return snippet;
        }

    }

    class ExistingIngredientSnippet {
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
    }
}
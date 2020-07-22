package com.breakfastseta.foodcache.inventory;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EditItem extends AppCompatActivity {
    //TODO edit item 0 should delete
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editTextIngredient;
    private RecyclerView recyclerView;
    private String units;

    private EditQuantityAdapter adapter;

    private String path;
    private static final String TAG = "EditItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit Item");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        editTextIngredient = findViewById(R.id.edit_ingredient);
        recyclerView = findViewById(R.id.recycler_view);

        path = getIntent().getStringExtra("path");

        assert path != null;
        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        setTextView(document);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // set TextView's using data from document snapshot
    private void setTextView(DocumentSnapshot document) {
        String ingredient = document.getString("ingredient");
        Double quantity = document.getDouble("quantity");
        Map<String, Double> expiryMap = (Map<String, Double>) document.get("expiryMap");
        units = document.getString("units");

        //Setting TextView's
        editTextIngredient.setText(ingredient);

        setUpRecyclerView(quantity, expiryMap, units);
    }

    private void setUpRecyclerView(Double quantity, Map<String, Double> expiryMap, String units) {
        ArrayList<String> keys = new ArrayList<>(expiryMap.keySet());
        Collections.sort(keys);

        adapter = new EditQuantityAdapter(keys, expiryMap, units, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnUpdateQuantityListener(new EditQuantityAdapter.EditQuantityListener() {
            @Override
            public void updateData() {
                Collections.sort(adapter.keys);
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void saveEditIngredient(View view) {

        if (getCurrentFocus() != null) {
            getCurrentFocus().clearFocus();
        };

        String ingredient = editTextIngredient.getText().toString();

        if (ingredient.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Double> expiryMap = adapter.map;
            Double quantity = 0.0;

            Map<String, Double> newExpiryMap = new HashMap<>();
            for (String key : expiryMap.keySet()) {
                Double value = expiryMap.get(key);
                if (value != 0.0) {
                    newExpiryMap.put(key, value);
                    quantity += value;
                }
            }

            // get new expiry date
            TreeMap<Date, Double> tree = new TreeMap<>();
            for (String s : newExpiryMap.keySet()) {
                tree.put(Inventory.stringToTimestamp(s).toDate(), newExpiryMap.get(s));
            }

            Timestamp dateTimestamp = null;
            if (!tree.isEmpty()) {
                dateTimestamp = new Timestamp(tree.firstKey());
            }
            
            DocumentReference docRef = db.document(path);

            if (quantity == 0.0) {
                docRef.delete();
            } else {
                Map<String, Object> update = new HashMap<>();
                update.put("ingredient", ingredient);
                update.put("quantity", quantity);
                update.put("dateTimestamp", dateTimestamp);
                update.put("expiryMap", newExpiryMap);
                update.put("nameLowerCase", ingredient.toLowerCase());
                docRef.update(update);
            }

            finish();
        }
    }

    public void addQuantity(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        // open date picker dialog
        DatePickerDialog picker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String s = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        Date expiry;
                        try {
                            expiry = simpleDateFormat.parse(s);

                            ArrayList<String> keys = adapter.keys;
                            Map<String, Double> map = adapter.map;

                            // updating data
                            String newKey = (new Timestamp(expiry)).toString();

                            if (!keys.contains(newKey)) {
                                // change current entry
                                keys.add(newKey);
                                Collections.sort(keys);
                                map.put(newKey, 0.0);
                                adapter.notifyDataSetChanged();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
        picker.show();
    }
}
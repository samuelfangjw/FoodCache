package com.breakfastseta.foodcache.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.DigitsInputFilter;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.List;

public class EditShoppingIngredient extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editShopName;
    private EditText editShopDescription;
    private EditText editShopQuantity;
    private NiceSpinner editShopUnits;

    List<String> unitsArr;

    private String path;
    private static final String TAG = "EditShoppingIngredient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_ingredient);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit Item");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        editShopName = findViewById(R.id.edit_shopitem_edit_name);
        editShopDescription = findViewById(R.id.edit_shopitem_edit_description);
        editShopQuantity = findViewById(R.id.edit_shopitem_quantity);
        editShopUnits = findViewById(R.id.spinner_edit_shoppingunits);

        unitsArr = Arrays.asList(getResources().getStringArray(R.array.units));
        editShopUnits.attachDataSource(unitsArr);

        editShopUnits.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                checkUnits();
            }
        });

        path = getIntent().getStringExtra("path");
        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        setTextView(document);
                        checkUnits();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void checkUnits() {
        String units = editShopUnits.getSelectedItem().toString();
        if (units.equals("kg")) {
            editShopQuantity.setFilters(DigitsInputFilter.DOUBLE_FILTER);
        } else {
            String text = editShopQuantity.getText().toString();
            if (text.contains(".")) {
                editShopQuantity.setText(Util.formatQuantityNumber(Double.parseDouble(text), units));
            }
            editShopQuantity.setFilters(DigitsInputFilter.INTEGER_FILTER);
        }
    }

    private void setTextView(DocumentSnapshot document) {
        String shoppingIngredientName = document.getString("itemName");
        String shoppingIngredientDescription = document.getString("description");
        double shoppingIngredientQuantity = document.getDouble("noItems");
        String shoppingIngredientUnits = document.getString("units");

        //Setting TextView's
        editShopName.setText(shoppingIngredientName);
        editShopDescription.setText(shoppingIngredientDescription);
        String quantity = "" + shoppingIngredientQuantity;
        editShopQuantity.setText(quantity);
        //Setting Spinner
        if (shoppingIngredientUnits != null) {
            int spinnerPosition = unitsArr.indexOf(shoppingIngredientUnits);
            editShopUnits.setSelectedIndex(spinnerPosition);
        }
    }

    public void saveEditedIngredient(View view) {
        String newShopIngredientName = editShopName.getText().toString();
        String newShopIngredientDescription = editShopDescription.getText().toString();
        String newShopIngredientQuantity = editShopQuantity.getText().toString();
        String newShopIngredientUnits = editShopUnits.getSelectedItem().toString();

        if (newShopIngredientName.trim().isEmpty() || newShopIngredientQuantity.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference docRef = db.document(path);
        docRef.update("itemName", newShopIngredientName.trim());
        docRef.update("description", newShopIngredientDescription);
        docRef.update("noItems", Double.parseDouble(newShopIngredientQuantity));
        docRef.update("units", newShopIngredientUnits);

        finish();
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
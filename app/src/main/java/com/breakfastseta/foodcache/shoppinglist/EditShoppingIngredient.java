package com.breakfastseta.foodcache.shoppinglist;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditShoppingIngredient extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editShopName;
    private EditText editShopDescription;
    private EditText editShopQuantity;
    private Spinner editShopUnits;

    ArrayAdapter<CharSequence> adapterUnits;

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

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUnits = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editShopUnits.setAdapter(adapterUnits);

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
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

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
            int spinnerPosition = adapterUnits.getPosition(shoppingIngredientUnits);
            editShopUnits.setSelection(spinnerPosition);
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
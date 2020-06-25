package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditShoppingIngredient extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editShopName;
    private EditText editShopDescription;
    private NumberPicker editShopQuantity;
    private Spinner editShopUnits;

    ArrayAdapter<CharSequence> adapterUnits;

    private String path;
    private static final String TAG = "EditShoppingIngredient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_ingredient);

        editShopName = findViewById(R.id.edit_shopitem_edit_name);
        editShopDescription = findViewById(R.id.edit_shopitem_edit_description);
        editShopQuantity = findViewById(R.id.edit_number_picker_quantity);
        editShopUnits = findViewById(R.id.spinner_edit_shoppingunits);

        editShopQuantity.setMinValue(1);
        editShopQuantity.setMaxValue(20);

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
        long shoppingIngredientQuantity = document.getLong("noItems");
        String shoppingIngredientUnits = document.getString("units");

        //Setting TextView's
        editShopName.setText(shoppingIngredientName);
        editShopDescription.setText(shoppingIngredientDescription);
        editShopQuantity.setValue((int) shoppingIngredientQuantity);
        //Setting Spinner
        if (shoppingIngredientUnits != null) {
            int spinnerPosition = adapterUnits.getPosition(shoppingIngredientUnits);
            editShopUnits.setSelection(spinnerPosition);
        }
    }

    public void saveEditedIngredient(View view) {
        String newShopIngredientName = editShopName.getText().toString();
        String newShopIngredientDescription = editShopDescription.getText().toString();
        int newShopIngredientQuantity = editShopQuantity.getValue();
        String newShopIngredientUnits = editShopUnits.getSelectedItem().toString();

        if (newShopIngredientName.trim().isEmpty()) {
            Toast.makeText(this, "Please insert Shopping Item name", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference docRef = db.document(path);
        docRef.update("itemName", newShopIngredientName);
        docRef.update("description", newShopIngredientDescription);
        docRef.update("noItems", newShopIngredientQuantity);
        docRef.update("units", newShopIngredientUnits);

        finish();
    }
}
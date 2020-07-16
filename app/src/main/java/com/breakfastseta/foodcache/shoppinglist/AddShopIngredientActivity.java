package com.breakfastseta.foodcache.shoppinglist;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddShopIngredientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextQuantity;
    private Spinner editUnits;

    ArrayAdapter<CharSequence> adapterUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shoppingitem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Item");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        editTextName = findViewById(R.id.edit_shopitem_name);
        editTextDescription = findViewById(R.id.edit_shopitem_description);
        editTextQuantity = findViewById(R.id.edit_shopitem_quantity);
        editUnits = findViewById(R.id.spinner_edit_shoppingunits);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUnits = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editUnits.setAdapter(adapterUnits);
    }

    public void saveShopIngredient(View view) {
        saveNote();
    }

    private void saveNote() {
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        String quantity = editTextQuantity.getText().toString();
        String units = editUnits.getSelectedItem().toString();

        if (name.trim().isEmpty() || quantity.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(uid)
                .collection("ShoppingList");
        notebookRef.add(new ShoppingListItem(name.trim(), description, Double.parseDouble(quantity), units));
        Toast.makeText(this, "Shopping Item added", Toast.LENGTH_SHORT).show();
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
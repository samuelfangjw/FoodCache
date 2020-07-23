package com.breakfastseta.foodcache.shoppinglist;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.DigitsInputFilter;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.List;

public class AddShopIngredientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextQuantity;
    private NiceSpinner editUnits;

    List<String> unitsArr;

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

        unitsArr = Arrays.asList(getResources().getStringArray(R.array.units));
        editUnits.attachDataSource(unitsArr);

        editUnits.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                checkUnits();
            }
        });

        checkUnits();
    }

    private void checkUnits() {
        String units = editUnits.getSelectedItem().toString();
        if (units.equals("kg")) {
            editTextQuantity.setFilters(DigitsInputFilter.DOUBLE_FILTER);
        } else {
            String text = editTextQuantity.getText().toString();
            if (text.contains(".")) {
                editTextQuantity.setText(Util.formatQuantityNumber(Double.parseDouble(text), units));
            }
            editTextQuantity.setFilters(DigitsInputFilter.INTEGER_FILTER);
        }
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
        String uid = App.getFamilyUID();

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
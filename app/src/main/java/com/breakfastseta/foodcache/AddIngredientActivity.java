package com.breakfastseta.foodcache;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;
import java.util.Objects;

public class AddIngredientActivity extends AppCompatActivity {
    private EditText editTextIngredient;
    private NumberPicker numberPickerQuantity;
    private Button addItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Ingredient");

        editTextIngredient = findViewById(R.id.edit_text_ingredient);
        numberPickerQuantity = findViewById(R.id.number_picker_quantity);
        addItemButton = findViewById(R.id.add_item);

        numberPickerQuantity.setMinValue(0);
        numberPickerQuantity.setMaxValue(50);

    }

    public void addNote(View view) {
        String ingredient = editTextIngredient.getText().toString();
        int quantity = numberPickerQuantity.getValue();

        // trim removes empty spaces
        if (ingredient.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            CollectionReference inventoryRef = FirebaseFirestore.getInstance()
                    .collection("Inventory");
            inventoryRef.add(new Item(ingredient, quantity));
            Toast.makeText(this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }
}

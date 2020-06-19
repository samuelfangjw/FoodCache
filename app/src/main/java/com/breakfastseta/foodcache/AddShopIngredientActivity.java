package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddShopIngredientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private NumberPicker numberPickerQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shoppingitem);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Shopping List Item");

        editTextName = findViewById(R.id.edit_shopitem_name);
        editTextDescription = findViewById(R.id.edit_shopitem_description);
        numberPickerQuantity = findViewById(R.id.number_picker_quantity);

        numberPickerQuantity.setMinValue(1);
        numberPickerQuantity.setMaxValue(20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.shopping_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveShopIngredient(View view) {
        saveNote();
    }

    private void saveNote() {
        String name = editTextName.getText().toString();
        String description = editTextDescription.getText().toString();
        int quantity = numberPickerQuantity.getValue();

        if (name.trim().isEmpty()) {
            Toast.makeText(this, "Please insert Shopping Item name", Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionReference notebookRef = FirebaseFirestore.getInstance()
                .collection("ShoppingList");
        notebookRef.add(new ShoppingListItem(name, description, quantity));
        Toast.makeText(this, "Shopping Item added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
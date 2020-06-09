package com.breakfastseta.foodcache;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddIngredientActivity extends AppCompatActivity {
    private static final int SCAN_REQUEST = 10;

    private EditText editTextIngredient;
    private NumberPicker numberPickerQuantity;
    private TextView textViewExpiryDate;
    private Date date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Ingredient");

        editTextIngredient = findViewById(R.id.edit_text_ingredient);
        numberPickerQuantity = findViewById(R.id.number_picker_quantity);
        textViewExpiryDate = findViewById(R.id.expiry_date);

        numberPickerQuantity.setMinValue(0);
        numberPickerQuantity.setMaxValue(50);

    }

    public void addNote(View view) {
        String ingredient = editTextIngredient.getText().toString();
        int quantity = numberPickerQuantity.getValue();

        // trim removes empty spaces
        if (ingredient.trim().isEmpty() || date == null) {
            Toast.makeText(this, "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            Timestamp dateTimestamp = new Timestamp(date);
            CollectionReference inventoryRef = FirebaseFirestore.getInstance()
                    .collection("Inventory");
            inventoryRef.add(new Item(ingredient, quantity, dateTimestamp));
            Toast.makeText(this, "Note Added Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickDate(View view) {
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
                        try {
                            date = simpleDateFormat.parse(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        textViewExpiryDate.setText(s);
                    }
                }, year, month, day);
        picker.show();
    }

    public void scanItem(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCAN_REQUEST) {
            Log.d("Scan", "" + resultCode);
            if (resultCode == RESULT_OK) {
                String barcode = data.getStringExtra("Barcode");
                Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();
                Log.d("Scan", "Add Activity: " + barcode);
            }
        }
    }
}
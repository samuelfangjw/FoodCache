package com.breakfastseta.foodcache;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddIngredientActivity extends AppCompatActivity {
    private static final int SCAN_REQUEST = 10;

    private EditText editTextIngredient;
    private EditText editTextQuantity;
    private TextView textViewExpiryDate;
    private Spinner spinnerTab;
    private EditText editTextBarcode;

    private Date date = null;
    private boolean barcodeNotFound = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference barcodeRef = db.collection("Barcodes");
    private CollectionReference inventoryRef = db.collection("Inventory");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Ingredient");

        editTextIngredient = findViewById(R.id.edit_text_ingredient);
        editTextQuantity = findViewById(R.id.edit_text_quantity);
        textViewExpiryDate = findViewById(R.id.expiry_date);
        spinnerTab = findViewById(R.id.tab_spinner);
        editTextBarcode = findViewById(R.id.edit_text_barcode);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tabs, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerTab.setAdapter(adapter);
    }

    public void addNote(View view) {
        String ingredient = editTextIngredient.getText().toString();
        String quantityString = editTextQuantity.getText().toString();
        String tab = spinnerTab.getSelectedItem().toString();

        // trim removes empty spaces
        if (ingredient.trim().isEmpty() || date == null || quantityString.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            // updating barcode database if barcode not found
            if (barcodeNotFound) {
                String barcode = editTextBarcode.getText().toString();
                Map<String, Object> docData = new HashMap<>();
                docData.put("Name", ingredient);
                barcodeRef.document(barcode).set(docData);
            }
            int quantity = Integer.parseInt(quantityString);
            Timestamp dateTimestamp = new Timestamp(date);
            inventoryRef.document(tab).collection("Ingredients").add(new Item(ingredient, quantity, dateTimestamp));
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
                assert data != null;
                String barcode = data.getStringExtra("Barcode");
                editTextBarcode.setText(barcode);
                checkBarcode(barcode);
            }
        }
    }

    //checks if barcode is stored in database and updates database if present
    private void checkBarcode(String barcode) {
        barcodeRef.document(barcode).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot document = task.getResult();
                                                   assert document != null;
                                                   if (document.exists()) {
                                                       Log.d("AddIngredient", "Document exists!");
                                                       String name = document.getString("Name");
                                                       editTextIngredient.setText(name);
                                                   } else {
                                                       barcodeNotFound = true;
                                                       Toast.makeText(AddIngredientActivity.this, "No Matching Product Found", Toast.LENGTH_SHORT).show();
                                                       Log.d("AddIngredient", "Document does not exist!");
                                                   }
                                               } else {
                                                   Log.d("AddIngredient", "Failed with: ", task.getException());
                                               }
                                           }
                                       }
                );
    }

    //for debugging purposes. will remove
    public void debugBarcode(View view) {
        String s = editTextBarcode.getText().toString();
        checkBarcode(s);
    }
}
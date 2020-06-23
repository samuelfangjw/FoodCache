package com.breakfastseta.foodcache;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditItem extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editTextIngredient;
    private EditText editTextQuantity;
    private TextView textViewExpiry;
    private TextView textViewDaysLeft;
    private Spinner spinnerUnits;

    Date expiry;

    ArrayAdapter<CharSequence> adapterUnits;

    private String path;
    private static final String TAG = "EditItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editTextIngredient = findViewById(R.id.edit_ingredient);
        editTextQuantity = findViewById(R.id.edit_quantity);
        textViewExpiry = findViewById(R.id.edit_expiry_date);
        textViewDaysLeft = findViewById(R.id.edit_days_left);
        spinnerUnits = findViewById(R.id.spinner_edit_units);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUnits = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerUnits.setAdapter(adapterUnits);

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
        Long quantity = document.getLong("quantity");
        Timestamp timestamp = document.getTimestamp("dateTimestamp");
        String units = document.getString("units");

        //Formatting Date
        assert timestamp != null;
        expiry = timestamp.toDate();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String expiryString = dateFormat.format(expiry);

        //Calculating Days Left
        Date now = new Date();
        long diffInMillies = Math.abs(expiry.getTime() - now.getTime());
        int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        //Formatting Strings
        String daysLeft = "Days Left: " + diff;
        String quantityString = "" + quantity;

        //Setting Spinner
        if (units != null) {
            int spinnerPosition = adapterUnits.getPosition(units);
            spinnerUnits.setSelection(spinnerPosition);
        }

        //Setting TextView's
        editTextIngredient.setText(ingredient);
        editTextQuantity.setText(quantityString);
        textViewExpiry.setText(expiryString);
        textViewDaysLeft.setText(daysLeft);
    }


    public void saveEditIngredient(View view) {
        String ingredient = editTextIngredient.getText().toString();
        String quantityString = editTextQuantity.getText().toString();
        String dateString = textViewExpiry.getText().toString();
        String units = spinnerUnits.getSelectedItem().toString();

        // trim removes empty spaces
        if (ingredient.trim().isEmpty() || dateString.trim().isEmpty() || quantityString.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            int quantity = Integer.parseInt(quantityString);
            Timestamp dateTimestamp = new Timestamp(expiry);

            DocumentReference docRef = db.document(path);
            docRef.update("ingredient", ingredient);
            docRef.update("quantity", quantity);
            docRef.update("dateTimestamp", dateTimestamp);
            docRef.update("units", units);

            finish();
        }
    }

    public void pickDate(View view) {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date expiryDate = new Date();
        try {
            expiryDate = dateFormat.parse(textViewExpiry.getText().toString());
        } catch (ParseException e){
            e.printStackTrace();
        }

        final Calendar cldr = Calendar.getInstance();
        cldr.setTime(expiryDate);
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
                            expiry = simpleDateFormat.parse(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        textViewExpiry.setText(dateFormat.format(expiry));

                        // set days left
                        Date now = new Date();
                        long diffInMillies = Math.abs(expiry.getTime() - now.getTime());
                        int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        String daysLeft = "Days Left: " + diff;
                        textViewDaysLeft.setText(daysLeft);
                    }
                }, year, month, day);
        picker.show();
    }
}
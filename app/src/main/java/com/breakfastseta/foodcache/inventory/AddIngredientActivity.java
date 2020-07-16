package com.breakfastseta.foodcache.inventory;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddIngredientActivity extends AppCompatActivity {
    private static final int SCAN_REQUEST = 10;
    private static final String TAG = "AddIngredientActivity";

    private EditText editTextIngredient;
    private EditText editTextQuantity;
    private TextView textViewExpiryDate;
    private Spinner spinnerUnits;
    private Spinner spinnerTab;
    private EditText editTextBarcode;

    private Date date = null;
    private String barcode = null;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private CollectionReference barcodeRef = db.collection("Users").document(uid).collection("Barcodes");

    ArrayAdapter<CharSequence> adapterUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Item");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        editTextIngredient = findViewById(R.id.edit_text_ingredient);
        editTextQuantity = findViewById(R.id.edit_text_quantity);
        textViewExpiryDate = findViewById(R.id.expiry_date);
        spinnerUnits = findViewById(R.id.spinner_units);
        spinnerTab = findViewById(R.id.tab_spinner);
        editTextBarcode = findViewById(R.id.edit_text_barcode);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUnits = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerUnits.setAdapter(adapterUnits);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tabs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTab.setAdapter(adapter);
    }

    public void addNote(View view) {
        final String ingredient = editTextIngredient.getText().toString();
        String quantityString = editTextQuantity.getText().toString();
        final String tab = spinnerTab.getSelectedItem().toString();
        final String units = spinnerUnits.getSelectedItem().toString();

        // trim removes empty spaces
        if (ingredient.trim().isEmpty() || date == null || quantityString.trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            final double quantity = Double.parseDouble(quantityString);
            Date now = new Date();
            final long expiryDays = date.getTime() - now.getTime();

            final Map<String, Object> docData = new HashMap<>();
            docData.put("Name", ingredient);
            docData.put("expiryDays", expiryDays);
            docData.put("quantity", quantity);
            docData.put("units", units);
            docData.put("location", tab);

            Query query = barcodeRef.whereEqualTo("Name", ingredient).limit(1);

            // updating barcode database if barcode found
            if (barcode != null) {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            for (QueryDocumentSnapshot document : result) {
                                String path = document.getReference().getPath();
                                db.document(path).delete();
                            }
                            barcodeRef.document(barcode).set(docData);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

            } else { // update document with matching field, else create new document
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result.isEmpty()) { // no matching document found
                                barcodeRef.add(docData);
                            } else { // matching document found
                                for (QueryDocumentSnapshot document : result) {
                                    String path = document.getReference().getPath();
                                    db.document(path).set(docData);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
            Timestamp dateTimestamp = new Timestamp(date);
            Inventory.create().addIngredient(new Item(ingredient.trim(), quantity, dateTimestamp, units, tab)).setListener(new Inventory.OnFinishListener() {
                @Override
                public void onFinish() {
                    Toast.makeText(AddIngredientActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void pickDate(View view) {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        final Calendar cldr = Calendar.getInstance();
        if (date != null) {
            cldr.setTime(date);
        }
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
                            textViewExpiryDate.setText(dateFormat.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
        picker.show();
    }

    public void scanItem(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                barcode = result.getContents();
                editTextBarcode.setText(barcode);
                checkBarcode(barcode);
                Toast.makeText(this, "Scanned: " + barcode, Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
                                                       long expiry = document.getLong("expiryDays");
                                                       double quantity = document.getDouble("quantity");
                                                       String units = document.getString("units");

                                                       // Calculating date
                                                       Date now = new Date();
                                                       long timeInMillies = now.getTime() + expiry;
                                                       date = new Date(timeInMillies);
                                                       SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                                                       String expiryString = dateFormat.format(date);

                                                       editTextIngredient.setText(name);
                                                       editTextQuantity.setText(String.valueOf(quantity));
                                                       textViewExpiryDate.setText(expiryString);

                                                       if (units != null) {
                                                           int spinnerPosition = adapterUnits.getPosition(units);
                                                           spinnerUnits.setSelection(spinnerPosition);
                                                       }
                                                   } else {
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
        barcode = s;
        checkBarcode(s);
    }
}
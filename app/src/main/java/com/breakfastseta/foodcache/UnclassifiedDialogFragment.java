package com.breakfastseta.foodcache;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UnclassifiedDialogFragment extends DialogFragment {

    private static final String TAG = "UnclassifiedDialogFragm";

    private TextView expiryDateView;
    private TextView ingredientView;
    private Spinner spinnerTab;
    private Date date;
    private String path;
    DocumentSnapshot document;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference inventoryRef = db.collection("Inventory");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        path = bundle.getString("path");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater and view
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_unclassified_dialog, null);

        // Customising View
        ingredientView = view.findViewById(R.id.unclassified_ingredient_name);
        expiryDateView = view.findViewById(R.id.unclassified_expiry_date);
        spinnerTab = view.findViewById(R.id.unclassified_spinner_tab);

        expiryDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(v);
            }
        });

        ArrayAdapter<CharSequence> adapterUnits = ArrayAdapter.createFromResource(getContext(),
                R.array.tabs, android.R.layout.simple_spinner_item);
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTab.setAdapter(adapterUnits);

        // Set Ingredient Name
        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    ingredientView.setText((String) document.get("Name"));
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Building dialog box
        builder.setView(view)
                .setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addItem(document);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void addItem(DocumentSnapshot document) {
        String ingredient = document.getString("Name");
        long quantity = document.getLong("quantity");
        String units = document.getString("units");
        String tab = spinnerTab.getSelectedItem().toString();

        // trim removes empty spaces
        if (date == null) {
            Toast.makeText(getContext(), "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            // updating barcode database if barcode not found
            Timestamp dateTimestamp = new Timestamp(date);
            inventoryRef.document(tab).collection("Ingredients")
                    .add(new Item(ingredient,(int) quantity, dateTimestamp, units))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            db.document(path).delete();
                        }
                    });
        }
    }

    public void pickDate(View view) {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // open date picker dialog
        DatePickerDialog picker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String s = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        try {
                            date = simpleDateFormat.parse(s);
                            expiryDateView.setText(dateFormat.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
        picker.show();
    }
}
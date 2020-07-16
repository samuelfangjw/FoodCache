package com.breakfastseta.foodcache.inventory;

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

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.Inventory;
import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.HashMap;
import java.util.Map;

public class UnclassifiedDialogFragment extends DialogFragment {

    private static final String TAG = "UnclassifiedDialogFragm";

    private TextView expiryDateView;
    private TextView ingredientView;
    private Spinner spinnerTab;
    private Date date;
    private String path;
    DocumentSnapshot document;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid = App.getFamilyUID();
    private CollectionReference barcodeRef = db.collection("Users").document(uid).collection("Barcodes");

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

        ArrayAdapter<String> adapterUnits = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, App.getTabs());
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
        double quantity = document.getDouble("quantity");
        String units = document.getString("units");
        String tab = spinnerTab.getSelectedItem().toString();

        Date now = new Date();
        long expiryDays = date.getTime() - now.getTime();
        final Map<String, Object> docData = new HashMap<>();
        docData.put("Name", ingredient);
        docData.put("expiryDays", expiryDays);
        docData.put("quantity", quantity);
        docData.put("units", units);
        docData.put("location", tab);

        // trim removes empty spaces
        if (date == null) {
            Toast.makeText(getContext(), "Please fill in all values", Toast.LENGTH_SHORT).show();
        } else {
            // Removing time part from date
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date = cal.getTime();

            Timestamp dateTimestamp = new Timestamp(date);
            Inventory.create()
                    .addIngredient(new Item(ingredient, quantity, dateTimestamp, units, tab))
                    .setListener(new Inventory.OnFinishListener() {
                        @Override
                        public void onFinish() {
                            db.document(path).delete();
                            barcodeRef.add(docData);
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
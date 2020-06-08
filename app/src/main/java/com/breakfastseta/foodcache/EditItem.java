package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EditItem extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView textViewIngredient;
    TextView textViewQuantity;
    TextView textViewExpiry;
    TextView textViewDaysLeft;

    private String path;
    private static final String TAG = "EditItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        textViewIngredient = findViewById(R.id.edit_ingredient);
        textViewQuantity = findViewById(R.id.edit_quantity);
        textViewExpiry = findViewById(R.id.edit_expiry_date);
        textViewDaysLeft = findViewById(R.id.edit_days_left);

        path = getIntent().getStringExtra("path");

        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
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

        //Formatting Date
        Date expiry = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String expiryString = dateFormat.format(expiry);

        //Calculating Days Left
        Date now = new Date();
        long diffInMillies = Math.abs(expiry.getTime() - now.getTime());
        int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        //Formatting Strings
        String daysLeft = "Days Left: " + String.valueOf(diff);
        String quantityString = "Quantity: " + String.valueOf(quantity);
        String expiryDate= "Expiry Date: " + expiryString;

        //Setting TextView's
        textViewIngredient.setText(ingredient);
        textViewQuantity.setText(quantityString);
        textViewExpiry.setText(expiryDate);
        textViewDaysLeft.setText(daysLeft);
    }
}
package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String KEY_INGREDIENT = "ingredient";
    private static final String KEY_Expiry = "expiry_date";

    private EditText editTextIngredient;
    private EditText editTextExpiry;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference itemRef = db.document("Items/firstitem");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextIngredient =  findViewById(R.id.ingredient);
        editTextExpiry =  findViewById(R.id.expiry_date);
        textViewData = findViewById(R.id.result);
    }

    public void save(View view) {
        String ingredient = editTextIngredient.getText().toString();
        String expiry = editTextExpiry.getText().toString();

        Map<String, Object> item = new HashMap<>();
        item.put(KEY_INGREDIENT, ingredient);
        item.put(KEY_Expiry, expiry);

        itemRef.set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Successfully Added Item To Database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void load(View view) {
        itemRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String Ingredient = documentSnapshot.getString(KEY_INGREDIENT);
                            String Expiry = documentSnapshot.getString(KEY_Expiry);

                            textViewData.setText("Ingredient: " + Ingredient + "\n" + "Expiry Date: " + Expiry);
                        } else {
                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }
}

package com.breakfastseta.foodcache;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        //Displaying user auth for debugging purposes
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String message = auth.toString();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // signOut and delete for debugging purposes
    public void delete(View view) {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

        //Displaying user auth for debugging purposes
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String message = auth.toString();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void signOut(View view) {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    // ...
                }
            });

        //Displaying user auth for debugging purposes
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String message = auth.toString();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

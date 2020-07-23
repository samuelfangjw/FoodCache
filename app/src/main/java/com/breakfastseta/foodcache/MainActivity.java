package com.breakfastseta.foodcache;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import com.breakfastseta.foodcache.inventory.FoodcacheActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            // Start signed in Activity
            startMainActivity();
        } else {
            // User is not signed in, prompt user to sign in
            createSignInIntent();
        }
    }

    // Starts the main activity after user has been authenticated
    private void startMainActivity() {
        App.setOnAppCompleteListener(new App.OnAppCompleteListener() {
            @Override
            public void onComplete() {
                Intent intent = new Intent(MainActivity.this, FoodcacheActivity.class);
                startActivity(intent);
                finish();
            }
        });
        App.afterAuthenticated(this);
    }

    // Open sign in page
    public void createSignInIntent() {
        // Firebase Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        // Create and launch sign-in intent, calls onActivityResult automatically
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_foodcache)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
    }

    // process result of sign in request
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Toast toast;
        // Successfully signed in
        if (resultCode == RESULT_OK) {
            startMainActivity();
        } else {
            // Sign in unsuccessful
            if (response == null) {
                Toast.makeText(this, "Sign in Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error signing in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.breakfastseta.foodcache;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    
    ImageView profilePicture;
    TextView nameTextView;
    TextView emailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("User Profile");
        
        profilePicture = findViewById(R.id.profile_picture);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        Button editButton =findViewById(R.id.edit_profile);

        List<? extends UserInfo> providerdata = user.getProviderData();
        Log.d(TAG, providerdata.toString());
        if (providerdata.size() > 1) {
            UserInfo info = providerdata.get(1);
            String id = info.getProviderId();
            Log.d(TAG, "onCreate: " + id);
            if (id.equals("password") ) {
                editButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (user != null) {
            updateUserProfile();
        }
    }

    private void updateUserProfile() {
        Uri profilePhoto = user.getPhotoUrl();
        String name = user.getDisplayName();
        String email = user.getEmail();

        if (profilePhoto != null) {
            Glide.with(this)
                    .load(profilePhoto)
                    .into(profilePicture);
        }

        if (name != null) {
            nameTextView.setText(name);
        }

        if (email != null) {
            emailTextView.setText(email);
        }
    }

    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    }
                });
    }

    public void delete(View view) {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    }
                });
    }

    public void editProfile(View view) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }
}

package com.breakfastseta.foodcache.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.MainActivity;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final int RESULT_CODE = 10;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    
    ImageView profilePicture;
    TextView nameTextView;
    TextView emailTextView;

    byte[] picture = null;
    String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.createToolbar(this, toolbar);
        
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

        if (user != null) {
            updateUserProfile();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (picture != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            profilePicture.setImageBitmap(bitmap);
        }
        if (name != null) {
            nameTextView.setText(name);
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
        Bitmap bitmap = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
        bitmap = Util.cropToSquare(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] image = baos.toByteArray();
        String name = nameTextView.getText().toString();

        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("picture", image);
        intent.putExtra("name", name);
        startActivityForResult(intent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            picture = data.getByteArrayExtra("picture");
            name = data.getStringExtra("name");
        }
    }

}

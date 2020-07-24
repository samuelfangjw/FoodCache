package com.breakfastseta.foodcache.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.MainActivity;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileActivity extends AppCompatActivity {

    Profile profile = App.getProfile();

    LinearLayout linearLayout;
    ImageView profilePicture;
    TextView nameTextView;
    TextView emailTextView;
    TextView friendsCount;
    TextView recipesCount;
    TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Util.createToolbar(this, toolbar);
        setTitle("Profile");
        
        profilePicture = findViewById(R.id.profile_picture);
        nameTextView = findViewById(R.id.name);
        emailTextView = findViewById(R.id.email);
        friendsCount = findViewById(R.id.friends);
        recipesCount = findViewById(R.id.recipes);
        usernameTextView = findViewById(R.id.username);
        linearLayout = findViewById(R.id.linear_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUserProfile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (App.getAuthProvider() == App.ANONYMOUS) {
            menu.findItem(R.id.action_edit).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editProfile();
                return true;
            case R.id.action_delete:
                delete();
                return true;
            case R.id.action_log_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUserProfile() {
        Uri profilePhoto = profile.getUri();
        String name = profile.getName();
        String email = profile.getEmail();
        String username = profile.getUsername();

        if (profilePhoto != null) {
            Glide.with(this)
                    .load(profilePhoto)
                    .placeholder(R.drawable.ic_profile_pic)
                    .fallback(R.drawable.ic_profile_pic)
                    .into(profilePicture);
        }

        nameTextView.setText(name);
        if (username != null) {
            usernameTextView.setText("Username: " + username);
        } else {
            linearLayout.removeView(usernameTextView);
        }
        emailTextView.setText("Email: "+ email);
        friendsCount.setText("" + profile.getFriendsCount());
        Long recipeCount = profile.getRecipeCount();
        if (recipeCount == null) {
            recipeCount = 0L;
        }
        recipesCount.setText("" + recipeCount);
    }

    public void signOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Delete account failed. Please try logging in and out again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void editProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

}

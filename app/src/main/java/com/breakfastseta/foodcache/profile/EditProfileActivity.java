package com.breakfastseta.foodcache.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tobiasschuerg.prefixsuffix.PrefixSuffixEditText;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    Profile profile = App.getProfile();

    ImageView profilePicture;
    PrefixSuffixEditText nameEditText;
    PrefixSuffixEditText usernameEditText;
    FrameLayout loadingScreen;
    MaterialButton button;

    Uri resultUri;
    private boolean overrideBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);
        setTitle("Edit Profile");

        profilePicture = findViewById(R.id.profile_picture);
        nameEditText = findViewById(R.id.name);
        usernameEditText = findViewById(R.id.username);
        loadingScreen = findViewById(R.id.loading_overlay);
        button = findViewById(R.id.button);

        setViews();
    }

    private void setViews() {
        Uri profilePhoto = profile.getUri();
        String name = profile.getName();
        String username = profile.getUsername();

        if (profilePhoto != null) {
            Glide.with(this)
                    .load(profilePhoto)
                    .placeholder(R.drawable.ic_profile_pic)
                    .fallback(R.drawable.ic_profile_pic)
                    .into(profilePicture);
        }

        nameEditText.setText(name);
        if (username != null) {
            usernameEditText.setText(username);
        }
    }

    public void showLoadingScreen() {
        overrideBack = true;
        loadingScreen.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        overrideBack = false;
        loadingScreen.setVisibility(View.INVISIBLE);
    }

    public void editPicture(View view) {
        // start picker to get image for cropping and then use the image in cropping activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setInitialCropWindowPaddingRatio(0)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(true)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                setImage();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setImage() {
        profilePicture.setImageURI(resultUri);
        Log.d(TAG, "setImage: " + resultUri);
    }


    public void save(View view) {
        //TODO DATA VALIDATION
        button.clearFocus();
        showLoadingScreen();
        if (resultUri != null) {
            uploadPicture();
        } else {
            saveProfile();
        }
    }

    private void uploadPicture() {
        String uid = App.getUID();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(uid + "jpeg");

        reference.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        resultUri = uri;
                        saveProfile();
                    }
                });
    }

    private void saveProfile() {
        String name = nameEditText.getText().toString();
        String username = usernameEditText.getText().toString();

        profile.setName(name);
        profile.setUsername(username);
        profile.setPhotoURL(resultUri.toString());

        uploadProfile();
    }

    private void uploadProfile() {
        DocumentReference profileRef = App.getProfileRef();
        Map<String, Object> map = new HashMap<>();
        map.put("name", profile.getName());
        map.put("photoURL", profile.getPhotoURL());
        map.put("username", profile.getUsername());
        profileRef.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideLoadingScreen();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!overrideBack) {
            super.onBackPressed();
        }
    }
}
package com.breakfastseta.foodcache.social;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SocialAddPostActivity extends AppCompatActivity {

    private EditText requestItem;
    private EditText requestDesc;
    private EditText requestQuan;
    private Spinner requestUnits;

    private EditText blogTitle;
    private EditText blogDesc;
    private ImageButton blogImage;

    private RadioGroup radioGroup;
    private RadioButton button;

    private static final int Gallery_Pick = 1;

    private StorageReference postImageRef;

    private String saveCurrDate;
    private String saveCurrTime;
    private String postName;

    private Uri imageUri;

    private String downloadURL;

    ArrayAdapter<CharSequence> adapterUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_add_post);

        postImageRef = FirebaseStorage.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Post");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        ConstraintLayout reqLayout = findViewById(R.id.request_layout);
        ConstraintLayout blogLayout = findViewById(R.id.blogpost_layout);

        radioGroup = (RadioGroup) findViewById(R.id.type_radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.request) {
                    reqLayout.setVisibility(View.VISIBLE);
                    blogLayout.setVisibility(View.GONE);
                } else {
                    reqLayout.setVisibility(View.GONE);
                    blogLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        requestItem = findViewById(R.id.edit_ingredient_request);
        requestDesc = findViewById(R.id.edit_ingredient_description);
        requestQuan = findViewById(R.id.edit_ingredient_quantity);
        requestUnits = findViewById(R.id.spinner_edit_units);

        blogTitle = findViewById(R.id.edit_blog_title) ;
        blogDesc = findViewById(R.id.edit_blog_description);
        blogImage = findViewById(R.id.imageButton); //Need this?

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUnits = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        requestUnits.setAdapter(adapterUnits);

        blogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

    }

    private void StoreImageFirebase() {
        Calendar callDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrDate = currentDate.format(callDate.getTime());

        Calendar callTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrTime = currentTime.format(callTime.getTime());

        postName = saveCurrDate + saveCurrTime;


        final StorageReference filePath = postImageRef.child("Post Images")
                .child(imageUri.getLastPathSegment() + postName + ".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    downloadURL = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    Toast.makeText(SocialAddPostActivity.this,"Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    savePost();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SocialAddPostActivity.this,"Error Occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //TODO: set up showing post in main Social Activity

    public void postPost(View view) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.request) {
            savePost();
        } else {
            StoreImageFirebase();
        }
    }

    private void savePost() {
        if (radioGroup.getCheckedRadioButtonId() == R.id.request) {
            String reqName = requestItem.getText().toString();
            String reqDesc = requestDesc.getText().toString();
            String reqQuan = requestQuan.getText().toString();
            String reqUnits = requestUnits.getSelectedItem().toString();

            if (reqName.trim().isEmpty() || reqQuan.trim().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("SocialUsers")
                    .document(uid)
                    .collection("UserPosts");
            notebookRef.add(new SocialPost(SocialPostType.REQUESTPOST, reqName, reqDesc, Double.parseDouble(reqQuan), reqUnits));
            Toast.makeText(this, "Request Post added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            String reqName = blogTitle.getText().toString();
            String reqDesc = blogDesc.getText().toString();

            if (reqName.trim().isEmpty() || reqDesc.trim().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            CollectionReference notebookRef = FirebaseFirestore.getInstance()
                    .collection("SocialUsers")
                    .document(uid)
                    .collection("UserPosts");
            notebookRef.add(new SocialPost(SocialPostType.BLOGPOST, reqName, reqDesc, downloadURL));
            Toast.makeText(this, "Request Post added", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void OpenGallery() {
        Intent newIntent = new Intent();
        newIntent.setAction(Intent.ACTION_GET_CONTENT);
        newIntent.setType("image/*");
        startActivityForResult(newIntent, Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && data != null) {
            imageUri = data.getData();
            blogImage.setImageURI(imageUri);
        }
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
}
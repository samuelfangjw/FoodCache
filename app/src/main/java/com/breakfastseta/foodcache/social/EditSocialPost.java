package com.breakfastseta.foodcache.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EditSocialPost extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editRequestItem;
    private EditText editRequestDesc;
    private EditText editRequestQuan;
    private Spinner editRequestUnits;

    private EditText editBlogTitle;
    private EditText editBlogDesc;
    private ImageView editBlogImage;

    private TextView postTypeTextChange;

    ArrayAdapter<CharSequence> adapterUnits;

    private static final String TAG = "EditPost";

    private String path;

    private String currPostType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_social_post);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Edit Post");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        editRequestItem = findViewById(R.id.edit_post_edit_ingredient_request);
        editRequestDesc = findViewById(R.id.edit_post_edit_ingredient_description);
        editRequestQuan = findViewById(R.id.edit_post_edit_ingredient_quantity);
        editRequestUnits = findViewById(R.id.edit_post_spinner_edit_units);

        editBlogTitle = findViewById(R.id.edit_post_edit_blog_title);
        editBlogDesc = findViewById(R.id.edit_post_edit_blog_description);
        editBlogImage = findViewById(R.id.edit_post_photo);

        postTypeTextChange = findViewById(R.id.post_type_text_change);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUnits = ArrayAdapter.createFromResource(this,
                R.array.units, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editRequestUnits.setAdapter(adapterUnits);

        path = getIntent().getStringExtra("path");
        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
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

    private void setTextView(DocumentSnapshot document) {
        ConstraintLayout editReqLayout = findViewById(R.id.edit_post_request_layout);
        ConstraintLayout editBlogLayout = findViewById(R.id.edit_post_blogpost_layout);
        this.currPostType = document.getString("type");

        if (currPostType.equals(SocialPostType.REQUESTPOST.toString())) {
            postTypeTextChange.setText("Request Post");

            editReqLayout.setVisibility(View.VISIBLE);
            editBlogLayout.setVisibility(View.GONE);

            String reqPostItem = document.getString("name");
            String reqPostDesc = document.getString("desc");
            double reqPostQuan = document.getDouble("noItems");
            String reqPostUnits = document.getString("units");

            //setting TextView;
            editRequestItem.setText(reqPostItem);
            editRequestDesc.setText(reqPostDesc);
            String quantity = "" + reqPostQuan;
            editRequestQuan.setText(quantity);
            //setting Spinner
            if (reqPostUnits != null) {
                int spinnerPosition = adapterUnits.getPosition(reqPostUnits);
                editRequestUnits.setSelection(spinnerPosition);
            }
        } else {
            postTypeTextChange.setText("Blog Post");

            editReqLayout.setVisibility(View.GONE);
            editBlogLayout.setVisibility(View.VISIBLE);

            String blogPostItem = document.getString("name");
            String blogPostDesc = document.getString("desc");
            String blogPostPhoto = document.getString("downloadURL");

            //setting TextView
            editBlogTitle.setText(blogPostItem);
            editBlogDesc.setText(blogPostDesc);
            Picasso.get().load(blogPostPhoto).into(editBlogImage);
        }
    }

    public void saveEditPost(View view) {
        if (currPostType.equals(SocialPostType.REQUESTPOST.toString())) {
            String newEditReqName = editRequestItem.getText().toString();
            String newEditReqDesc = editRequestDesc.getText().toString();
            String newEditReqQuan = editRequestQuan.getText().toString();
            String newEditReqUnit = editRequestUnits.getSelectedItem().toString();

            if (newEditReqName.trim().isEmpty() || newEditReqQuan.trim().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentReference docRef = db.document(path);
            docRef.update("name", newEditReqName);
            docRef.update("desc", newEditReqDesc);
            docRef.update("noItems", Double.parseDouble(newEditReqQuan));
            docRef.update("units", newEditReqUnit);

            finish();
        } else {
            String newEditBlogName = editBlogTitle.getText().toString();
            String newEditBlogDesc = editBlogDesc.getText().toString();

            if (newEditBlogName.trim().isEmpty() || newEditBlogDesc.trim().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentReference docRef = db.document(path);
            docRef.update("name", newEditBlogName);
            docRef.update("desc",newEditBlogDesc);

            finish();
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
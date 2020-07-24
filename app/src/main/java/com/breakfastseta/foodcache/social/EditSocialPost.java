package com.breakfastseta.foodcache.social;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.breakfastseta.foodcache.DigitsInputFilter;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.List;

public class EditSocialPost extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private EditText editRequestItem;
    private EditText editRequestDesc;
    private EditText editRequestQuan;
    private NiceSpinner editRequestUnits;

    private EditText editBlogTitle;
    private EditText editBlogDesc;
    private ImageView editBlogImage;

    private TextView postTypeTextChange;

    List<String> unitsArr;

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

        unitsArr = Arrays.asList(getResources().getStringArray(R.array.units));
        editRequestUnits.attachDataSource(unitsArr);

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
        ScrollView editBlogLayout = findViewById(R.id.edit_post_blogpost_layout);
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
                int spinnerPosition = unitsArr.indexOf(reqPostUnits);
                editRequestUnits.setSelectedIndex(spinnerPosition);
            }

            checkUnits();

            editRequestUnits.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                @Override
                public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                    checkUnits();
                }
            });
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

    private void checkUnits() {
        String units = editRequestUnits.getSelectedItem().toString();
        if (units.equals("kg") || units.equals("Cups")) {
            editRequestQuan.setFilters(DigitsInputFilter.DOUBLE_FILTER);
        } else {
            String text = editRequestQuan.getText().toString();
            if (text.contains(".")) {
                editRequestQuan.setText(Util.formatQuantityNumber(Double.parseDouble(text), units));
            }
            editRequestQuan.setFilters(DigitsInputFilter.INTEGER_FILTER);
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
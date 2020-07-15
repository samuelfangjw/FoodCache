package com.breakfastseta.foodcache.recipe;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ViewRecipeActivity extends AppCompatActivity {
    private static final String TAG = "ViewRecipeActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    CollectionReference recipeRef = db.collection("Users").document(uid).collection("RecipeCache");

    private ImageView imageView;
    private TextView tv_author;
    private TextView tv_steps;
    private TextView tv_description;
    private TextView tv_cuisine;
    private TableLayout tableLayout;


    Toolbar toolbar;
    AppBarLayout appbarLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;

    Drawable save_icon;
    DocumentSnapshot documentSnapshot;
    String path;
    String imagePath;
    String name;
    String cuisine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        path = getIntent().getStringExtra("path");

        imageView = (ImageView) findViewById(R.id.recipe_image);
        tv_author = (TextView) findViewById(R.id.author);
        tv_steps = (TextView) findViewById(R.id.steps);
        tv_description = (TextView) findViewById(R.id.description);
        tv_cuisine = (TextView) findViewById(R.id.cuisine);
        tableLayout = (TableLayout) findViewById(R.id.table_layout);

        appbarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbarlayout);

        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Collapsed
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    toolbar.setBackgroundColor(getColor(R.color.primaryColor));
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
                    if (save_icon != null) {
                        save_icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                    }
                } else { // Expanded
                    toolbar.setBackgroundColor(Color.TRANSPARENT);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    if (save_icon != null) {
                        save_icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }
        });

        assert path != null;
        DocumentReference docRef = db.document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        documentSnapshot = document;
                        setTextView();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_recipe_menu, menu);
        save_icon = menu.findItem(R.id.action_save).getIcon();
        return true;
    }

    private void setTextView() {
        imagePath = documentSnapshot.getString("photo");
        String author = documentSnapshot.getString("author");
        name = documentSnapshot.getString("name");
        cuisine = documentSnapshot.getString("cuisine");
        ArrayList<Map<String, Object>> ingredients = (ArrayList<Map<String, Object>>) documentSnapshot.get("ingredients");
        ArrayList<String> steps = (ArrayList<String>) documentSnapshot.get("steps");
        String description = documentSnapshot.getString("description");

        if (imagePath != null) {
            Uri image_path = Uri.parse(imagePath);
            Glide.with(this)
                    .load(image_path)
                    .into(imageView);
        }

        getSupportActionBar().setTitle(name);
        String cuisineString = "Cuisine: " + cuisine;
        String authorString = "Author: " + author;
        Log.d(TAG, "setTextView: " + cuisineString);
        tv_author.setText(authorString);
        tv_cuisine.setText(cuisineString);
        tv_description.setText(description);

        for(Map<String, Object> i : ingredients) {
            String ingredient_name = (String) i.get("name");
            String ingredient_units = (String) i.get("units");
            double ingredient_quantity = (double) i.get("quantity");

            TableRow row = new TableRow(this);
            TextView tv1 = new TextView(this);
            TextView tv2 = new TextView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,8,8,8);
            tv1.setLayoutParams(params);
            tv2.setLayoutParams(params);

            tv1.setText(ingredient_name);
            String tv2Text = Util.formatQuantity(ingredient_quantity, ingredient_units);
            tv2.setText(tv2Text);
            row.addView(tv1);
            row.addView(tv2);

            tableLayout.addView(row);
        }

        StringBuilder steps_text = new StringBuilder();
        for (String s : steps) {
            steps_text.append(s).append("\n");
        }

        tv_steps.setText(steps_text.toString());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            case R.id.action_save:
                save_recipe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save_recipe() {
        ArrayList<String> viewers = (ArrayList<String>) documentSnapshot.get("viewers");
        // Check if recipe already saved
        if (viewers.contains(uid)) {
            Toast.makeText(this, "Recipe Already Saved!", Toast.LENGTH_SHORT).show();
        } else {
            db.document(path).update("viewers", FieldValue.arrayUnion(uid));
            recipeRef.document(cuisine).collection("Recipes").add(new RecipeSnippet(name, imagePath, path));
            Toast.makeText(this, "Recipe Saved Successfully!", Toast.LENGTH_SHORT).show();
        }

    }

}
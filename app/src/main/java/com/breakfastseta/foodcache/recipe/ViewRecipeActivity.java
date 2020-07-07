package com.breakfastseta.foodcache.recipe;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.breakfastseta.foodcache.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class ViewRecipeActivity extends AppCompatActivity {
    private static final String TAG = "ViewRecipeActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    String uid = user.getUid();

    private ImageView imageView;
    private TextView tv_name;
    private TextView tv_author;
    private TextView tv_ingredients;
    private TextView tv_steps;

    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);

        path = getIntent().getStringExtra("path");

        imageView = (ImageView) findViewById(R.id.recipe_image);
        tv_name = (TextView) findViewById(R.id.recipe_name);
        tv_author = (TextView) findViewById(R.id.author);
        tv_ingredients = (TextView) findViewById(R.id.ingredient);
        tv_steps = (TextView) findViewById(R.id.steps);

        assert path != null;
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
        String imagePath = document.getString("photo");
        String author = document.getString("author");
        String name = document.getString("name");
        ArrayList<Map<String, Object>> ingredients = (ArrayList<Map<String, Object>>) document.get("ingredients");
        ArrayList<String> steps = (ArrayList<String>) document.get("steps");

        if (imagePath != null) {
            Uri image_path = Uri.parse(imagePath);
            Glide.with(this)
                    .load(image_path)
                    .into(imageView);
        }

        tv_name.setText(name);
        tv_author.setText(author);

        StringBuilder ingredients_text = new StringBuilder();
        for(Map<String, Object> i : ingredients) {
            String ingredient_name = (String) i.get("name");
            String ingredient_units = (String) i.get("units");
            double ingredient_quantity = (double) i.get("quantity");
            String ingredient = ingredient_name + " " + ingredient_quantity + " " + ingredient_units;
            ingredients_text.append(ingredient).append("\n");
        }

        StringBuilder steps_text = new StringBuilder();
        for (String s : steps) {
            steps_text.append(s).append("\n");
        }

        tv_ingredients.setText(ingredients_text.toString());
        tv_steps.setText(steps_text.toString());
    }
}
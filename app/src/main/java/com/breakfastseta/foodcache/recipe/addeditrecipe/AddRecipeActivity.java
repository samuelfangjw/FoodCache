package com.breakfastseta.foodcache.recipe.addeditrecipe;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.breakfastseta.foodcache.App;
import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.recipe.Ingredient;
import com.breakfastseta.foodcache.recipe.Recipe;
import com.breakfastseta.foodcache.recipe.RecipeSnippet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class AddRecipeActivity extends AppCompatActivity
        implements AddRecipeFragmentOne.FragmentOneListener,
        AddRecipeFragmentTwo.FragmentTwoListener,
        AddRecipeFragmentThree.FragmentThreeListener {

    private static final String TAG = "AddRecipeActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    // Recipe fields
    Uri image_path = null;
    String imagePathString = null;
    Uri resultUri;
    String name;
    String author;
    String cuisine;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> steps;
    String description;

    boolean isPublic;

    // Fragments
    AddRecipeFragmentOne fragmentOne;
    AddRecipeFragmentTwo fragmentTwo;
    AddRecipeFragmentThree fragmentThree;

    FrameLayout loading_overlay;

    // Other fields
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

        fragmentOne = new AddRecipeFragmentOne();
        fragmentTwo = new AddRecipeFragmentTwo();
        fragmentThree = new AddRecipeFragmentThree();
        loading_overlay = findViewById(R.id.loading_overlay);

        showFragmentOne();

        setTitle("Add Recipe");
    }

    @Override
    public void nextFragmentOne(String name, String author, Uri resultUri, String cuisine, boolean isPublic, String description) {
        this.name = name;
        this.author = author;
        this.resultUri = resultUri;
        this.cuisine = cuisine;
        this.isPublic = isPublic;
        this.description = description;
        showFragmentTwo();
    }

    @Override
    public void nextFragmentTwo(ArrayList<Ingredient> arr) {
        this.ingredients = arr;
        showFragmentThree();
    }

    @Override
    public void nextFragmentThree(ArrayList<String> steps) {
        this.steps = steps;
        saveRecipe();
    }

    private void showFragmentOne() {
        current = 1;

        setTitle("Add Recipe");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentOne.isAdded()) { // if the fragment is already in container
            ft.show(fragmentOne);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_frame, fragmentOne, "One");
        }
        // Hide fragments
        if (fragmentTwo.isAdded()) {
            ft.hide(fragmentTwo);
        }
        if (fragmentThree.isAdded()) {
            ft.hide(fragmentThree);
        }
        ft.commit();
    }

    private void showFragmentTwo() {
        current = 2;

        setTitle("Add Ingredients");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentTwo.isAdded()) { // if the fragment is already in container
            ft.show(fragmentTwo);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_frame, fragmentTwo, "Two");
        }

        if (fragmentOne.isAdded()) {
            ft.hide(fragmentOne);
        }
        if (fragmentThree.isAdded()) {
            ft.hide(fragmentThree);
        }
        ft.commit();
    }

    private void showFragmentThree() {
        current = 3;

        setTitle("Add Recipe Steps");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragmentThree.isAdded()) { // if the fragment is already in container
            ft.show(fragmentThree);
        } else { // fragment needs to be added to frame container
            ft.add(R.id.fragment_frame, fragmentThree, "Three");
        }

        if (fragmentOne.isAdded()) {
            ft.hide(fragmentOne);
        }
        if (fragmentTwo.isAdded()) {
            ft.hide(fragmentTwo);
        }
        ft.commit();
    }

    private void showLoadingOverlay() {
        current = 4;
        loading_overlay.setVisibility(View.VISIBLE);
    }

    private void saveRecipe() {
        showLoadingOverlay();
        if (resultUri != null) {
            uploadImage();
        } else {
            uploadRecipe();
        }
    }

    private void uploadRecipe() {
        CollectionReference recipeRef = db.collection("Users").document(uid).collection("RecipeCache");
        ArrayList<String> viewers = new ArrayList<>();
        viewers.add(uid);
        if (image_path != null) {
            imagePathString = image_path.toString();
        }
        Recipe recipe = new Recipe(name, author, ingredients, steps, imagePathString, cuisine, uid, isPublic, viewers, description);

        db.collection("Recipes").add(recipe).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String path = task.getResult().getPath();
                recipeRef.document(cuisine).collection("Recipes").add(new RecipeSnippet(name, imagePathString, path, description));
                App.getProfile().addRecipeCount();
                finish();
            }
        });
    }

    private void uploadImage() {
        Date now = new Date();
        long time = now.getTime();
        String imageName = uid + time;

        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("recipe_images")
                .child(imageName);

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
                .addOnSuccessListener(uri -> {
                    image_path = uri;
                    uploadRecipe();
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
        if (current == 1) {
            super.onBackPressed();
        } else if (current == 2) {
            showFragmentOne();
        } else if (current == 3) {
            showFragmentTwo();
        }
    }

}
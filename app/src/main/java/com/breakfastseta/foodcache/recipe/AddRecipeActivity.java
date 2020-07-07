package com.breakfastseta.foodcache.recipe;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.breakfastseta.foodcache.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

//TODO adding recipes without image doesn't work
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
    byte[] picture;
    String name;
    String author;
    String cuisine;
    ArrayList<Ingredient> ingredients;
    ArrayList<String> steps;

    // Fragments
    AddRecipeFragmentOne fragmentOne;
    AddRecipeFragmentTwo fragmentTwo;
    AddRecipeFragmentThree fragmentThree;

    // Other fields
    int current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        fragmentOne = new AddRecipeFragmentOne();
        fragmentTwo = new AddRecipeFragmentTwo();
        fragmentThree = new AddRecipeFragmentThree();

        showFragmentOne();

        setTitle("Add Recipe");
    }

    @Override
    public void nextFragmentOne(String name, String author, byte[] picture, String cuisine) {
        this.name = name;
        this.author = author;
        this.picture = picture;
        this.cuisine = cuisine;
        showFragmentTwo();
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nextFragmentTwo(ArrayList<Ingredient> arr) {
        this.ingredients = ingredients;
        showFragmentThree();
    }

    @Override
    public void nextFragmentThree(ArrayList<String> steps) {
        this.steps = steps;
        saveRecipe();
    }


    private void showFragmentOne() {
        current = 1;

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

    private void saveRecipe() {
        if (picture != null) {
            uploadImage();
        } else {
            uploadRecipe();
        }
    }

    private void uploadRecipe() {
        CollectionReference recipeRef = db.collection("Users").document(uid).collection("RecipeCache");
        Recipe recipe = new Recipe(name, author, ingredients, steps, image_path.toString());
        recipeRef.document(cuisine).collection("Recipes").add(recipe);
        finish();
    }

    private void uploadImage() {
        Date now = new Date();
        long time = now.getTime();
        String imageName = uid + time;

        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("recipe_images")
                .child(imageName);

        reference.putBytes(picture)
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
package com.breakfastseta.foodcache.recommend;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.breakfastseta.foodcache.R;
import com.breakfastseta.foodcache.Util;
import com.breakfastseta.foodcache.recipe.Ingredient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class RecommendActivity extends AppCompatActivity implements ParametersFragment.ParametersListener, DisplayFragment.DisplayListener {
    private static final String TAG = "RecommendActivity";

    // Fragments
    ParametersFragment parametersFragment;
    AlgorithmFragment algorithmFragment;
    DisplayFragment displayFragment;

    ArrayList<RecommendSnippet> arrRecipes = new ArrayList<>();
    ArrayList<IngredientSnippet> arrIngredient = new ArrayList<>();
    ArrayList<RecommendSnippet> arrResults;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recipeRef = db.collection("Recipes");
    private CollectionReference inventoryRef = db.collection("Users").document(uid).collection("Inventory");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Find a Recipe");
        Util.createToolbar(this, toolbar);

        parametersFragment = new ParametersFragment();
        algorithmFragment = new AlgorithmFragment();
        displayFragment = new DisplayFragment();

        showFragmentParameters();
    }

    @Override
    public void nextParameters() {
        showFragmentAlgorithm();
        startAlgorithm();
        Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
    }

    public void nextAlgorithm() {
        showFragmentDisplay();
        Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void nextDisplay() {
        Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
    }

    private void showFragmentParameters() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, parametersFragment);
        ft.commit();
    }

    private void showFragmentAlgorithm() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, algorithmFragment);
        ft.commit();
    }

    private void showFragmentDisplay() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_frame, displayFragment);
        ft.commit();

    }

    private void startAlgorithm() {
        recipeRef.whereEqualTo("isPublic", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            for (DocumentSnapshot document : snapshots) {
                                String imagePath = document.getString("photo");
                                String name = document.getString("name");
                                String cuisine = document.getString("cuisine");
                                String path = document.getReference().getPath();
                                ArrayList<Map<String, Object>> ingredients = (ArrayList<Map<String, Object>>) document.get("ingredients");
                                ArrayList<Ingredient> ingredientsArr = new ArrayList<>();

                                assert ingredients != null;
                                for (Map<String, Object> i : ingredients) {
                                    String ingredient_name = (String) i.get("name");
                                    String ingredient_units = (String) i.get("units");
                                    double ingredient_quantity = (double) i.get("quantity");
                                    ingredientsArr.add(new Ingredient(ingredient_name, ingredient_quantity, ingredient_units));
                                }

                                arrRecipes.add(new RecommendSnippet(name, imagePath, path, cuisine, ingredientsArr));
                            }
                            getPrivateRecipes();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void getPrivateRecipes() {
        recipeRef.whereEqualTo("isPublic", false).whereArrayContains("viewers", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshots = task.getResult();
                        for (DocumentSnapshot document : snapshots) {
                            String imagePath = document.getString("photo");
                            String name = document.getString("name");
                            String cuisine = document.getString("cuisine");
                            String path = document.getReference().getPath();
                            ArrayList<Map<String, Object>> ingredients = (ArrayList<Map<String, Object>>) document.get("ingredients");
                            ArrayList<Ingredient> ingredientsArr = new ArrayList<>();

                            assert ingredients != null;
                            for (Map<String, Object> i : ingredients) {
                                String ingredient_name = (String) i.get("name");
                                String ingredient_units = (String) i.get("units");
                                double ingredient_quantity = (double) i.get("quantity");
                                ingredientsArr.add(new Ingredient(ingredient_name, ingredient_quantity, ingredient_units));
                            }

                            arrRecipes.add(new RecommendSnippet(name, imagePath, path, cuisine, ingredientsArr));
                        }
                        getIngredients();
                    }
                });
    }

    private void getIngredients() {
        inventoryRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot snapshots = task.getResult();

                long now = (new Date()).getTime() / 1000;

                for (DocumentSnapshot document : snapshots) {
                    Timestamp timestamp = document.getTimestamp("dateTimestamp");
                    String name = document.getString("ingredient");
                    double quantity = document.getDouble("quantity");
                    String units = document.getString("units");
                    long timeLeft = timestamp.getSeconds() - now + 86400;

                    arrIngredient.add(new IngredientSnippet(name, quantity, units, timeLeft));
                }

                runAlgorithm();
            }
        });
    }

    private void runAlgorithm() {
        arrResults = Algorithm.runAlgorithm(arrRecipes, arrIngredient);
        nextAlgorithm();
    }

}
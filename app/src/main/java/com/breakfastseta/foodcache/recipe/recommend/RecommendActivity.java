package com.breakfastseta.foodcache.recipe.recommend;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.breakfastseta.foodcache.App;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecommendActivity extends AppCompatActivity implements ParametersFragment.ParametersListener, DisplayFragment.DisplayListener {
    private static final String TAG = "RecommendActivity";

    // Fragments
    ParametersFragment parametersFragment;
    AlgorithmFragment algorithmFragment;
    DisplayFragment displayFragment;

    public static final int DEFAULT = 0;
    public static final int FRESH = 1;
    String cuisine;
    int settings;

    ArrayList<RecommendSnippet> arrRecipes = new ArrayList<>();
    ArrayList<IngredientSnippet> arrIngredient = new ArrayList<>();
    ArrayList<RecommendSnippet> arrResults;
    Map<String, Long> recipesPrepared = new HashMap<>();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference recipeRef = db.collection("Recipes");
    private CollectionReference inventoryRef = db.collection("Users").document(App.getFamilyUID()).collection("Inventory");


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
    public void nextParameters(String cuisine, int settings) {
        this.cuisine = cuisine;
        this.settings = settings;
        showFragmentAlgorithm();
        startAlgorithm();
    }

    public void nextAlgorithm() {
        showFragmentDisplay();
    }

    @Override
    public void nextDisplay() {

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
        Query query;

        if (cuisine.equals("All")) {
            query = recipeRef.whereEqualTo("isPublic", true);
        } else {
            query = recipeRef.whereEqualTo("cuisine", cuisine).whereEqualTo("isPublic", true);
        }
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            for (DocumentSnapshot document : snapshots) {
                                String imagePath = document.getString("photo");
                                String name = document.getString("name");
                                String cuisine = document.getString("cuisine");
                                String description = document.getString("description");
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

                                arrRecipes.add(new RecommendSnippet(name, imagePath, path, cuisine, ingredientsArr, description));
                            }
                            getPrivateRecipes();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void getPrivateRecipes() {
        Query query;

        if (cuisine.equals("All")) {
            query = recipeRef.whereEqualTo("isPublic", false).whereArrayContains("viewers", uid);
        } else {
            query = recipeRef.whereEqualTo("cuisine", cuisine).whereEqualTo("isPublic", false).whereArrayContains("viewers", uid);
        }

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot snapshots = task.getResult();
                        for (DocumentSnapshot document : snapshots) {
                            String imagePath = document.getString("photo");
                            String name = document.getString("name");
                            String cuisine = document.getString("cuisine");
                            String description = document.getString("description");
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

                            arrRecipes.add(new RecommendSnippet(name, imagePath, path, cuisine, ingredientsArr, description));
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
        if (settings == FRESH) {
            recipesPrepared = App.getProfile().getRecipesPrepared();
        }

        arrResults = Algorithm.runAlgorithm(arrRecipes, arrIngredient, recipesPrepared);
        nextAlgorithm();
    }

}
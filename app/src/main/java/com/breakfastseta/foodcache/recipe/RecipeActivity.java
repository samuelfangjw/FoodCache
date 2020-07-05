package com.breakfastseta.foodcache.recipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.breakfastseta.foodcache.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecipeActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;

    String[] tabs;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        tabs = getResources().getStringArray(R.array.cuisines);
        setTitle("My Recipe Cache");

        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(tabs[position]);
                    }
                }).attach();

//        add();
    }

    // TODO REMOVE THIS TOO
//    private void add() {
//        String tab = "Asian";
//        CollectionReference recipeRef = db.collection("Users").document(uid).collection("RecipeCache");
//        Ingredient first = new Ingredient("first", 1, "g");
//        Ingredient second = new Ingredient("second", 2, "kg");
//        ArrayList<Ingredient> arr = new ArrayList<Ingredient>();
//        arr.add(first);
//        arr.add(second);
//        recipeRef.document(tab).collection("Recipes").add(new Recipe("Name", "Author", arr, "Steps"));
//    }

    private RecipeViewPagerAdapter createCardAdapter() {
        RecipeViewPagerAdapter adapter = new RecipeViewPagerAdapter(this);
        return adapter;
    }
}
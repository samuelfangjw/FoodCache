package com.breakfastseta.foodcache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.inventory.FoodcacheActivity;
import com.breakfastseta.foodcache.profile.ProfileActivity;
import com.breakfastseta.foodcache.recipe.DiscoverRecipeActivity;
import com.breakfastseta.foodcache.recipe.RecipeActivity;
import com.breakfastseta.foodcache.shoppinglist.ShoppingListActivity;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.text.DecimalFormat;


public class Util {
    /*
    To create toolbar, add the following to activity:
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    setTitle("Title");
    Util.createToolbar(this, toolbar);

    Or this if just need close button:
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    setTitle("Title");
    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black);

    May need to override to get back button working:
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

    Add to layout:
    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:background="@color/primaryColor"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
    */
    public static Drawer createToolbar(Context context, Toolbar toolbar) {


        PrimaryDrawerItem itemFoodCache = new PrimaryDrawerItem().withIdentifier(1).withName("FoodCache");
        PrimaryDrawerItem itemShoppingList = new PrimaryDrawerItem().withIdentifier(2).withName("ShoppingList");
        PrimaryDrawerItem itemRecipe = new PrimaryDrawerItem().withIdentifier(3).withName("RecipeCache");
        PrimaryDrawerItem itemProfile = new PrimaryDrawerItem().withIdentifier(4).withName("Profile");
        PrimaryDrawerItem itemDiscover = new PrimaryDrawerItem().withIdentifier(5).withName("Discover Recipe");

        Drawer result = new DrawerBuilder()
                .withActivity((Activity) context)
                .withToolbar(toolbar)
                .addDrawerItems(
                        itemFoodCache,
                        itemShoppingList,
                        itemRecipe,
                        itemProfile,
                        new DividerDrawerItem(),
                        itemDiscover
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 0:
                                context.startActivity(new Intent(context, FoodcacheActivity.class));
                                return true;
                            case 1:
                                context.startActivity(new Intent(context, ShoppingListActivity.class));
                                return true;
                            case 2:
                                context.startActivity(new Intent(context, RecipeActivity.class));
                                return true;
                            case 3:
                                context.startActivity(new Intent(context, ProfileActivity.class));
                                return true;
                            case 5:
                                context.startActivity(new Intent(context, DiscoverRecipeActivity.class));
                                return true;
                        }
                        return false;
                    }
                })
                .build();
        return result;
    }
    // TODO Data validation for quantity (if int should not be able to type in double)
    // Helper class for formatting quantity based on units
    public static String formatQuantity(double quantity, String unit) {
        DecimalFormat intFormat = new DecimalFormat("#");
        DecimalFormat oneDPFormat = new DecimalFormat("0.0");

        switch (unit) {
            case "g":
            case "ml":
                return intFormat.format(quantity) + " " + unit;
            case "kg":
                return oneDPFormat.format(quantity) + " " + unit;
            case "Items":
                return intFormat.format(quantity);
            default:
                return "" + quantity + " " + unit;
        }
    }

    // Helper class for formatting quantity based on units
    public static String formatQuantityNumber(double quantity, String unit) {
        DecimalFormat intFormat = new DecimalFormat("#");
        DecimalFormat oneDPFormat = new DecimalFormat("0.0");

        switch (unit) {
            case "g":
            case "ml":
            case "Items":
                return intFormat.format(quantity);
            case "kg":
                return oneDPFormat.format(quantity);
            default:
                return "" + quantity;
        }
    }
}

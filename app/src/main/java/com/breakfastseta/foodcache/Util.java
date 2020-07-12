package com.breakfastseta.foodcache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.breakfastseta.foodcache.inventory.FoodcacheActivity;
import com.breakfastseta.foodcache.profile.ProfileActivity;
import com.breakfastseta.foodcache.recipe.DiscoverRecipeActivity;
import com.breakfastseta.foodcache.recipe.RecipeActivity;
import com.breakfastseta.foodcache.shoppinglist.ShoppingListActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.text.DecimalFormat;


public class Util {

    private static int selectedItem = 1;

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
        PrimaryDrawerItem itemDiscover = new PrimaryDrawerItem().withIdentifier(5).withName("Discover Recipes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri profilePhoto = user.getPhotoUrl();
        String name = user.getDisplayName();
        String email = user.getEmail();

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(imageView.getContext()).load(uri).centerCrop().into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Glide.with(imageView.getContext()).clear(imageView);
            }
        });

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity((Activity) context)
                .addProfiles(new ProfileDrawerItem().withName(name).withEmail(email).withIcon(profilePhoto))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesClickable(false)
                .build();

        Drawer result = new DrawerBuilder()
                .withAccountHeader(header)
                .withActivity((Activity) context)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withSelectedItem(selectedItem)
                .addDrawerItems(
                        itemFoodCache,
                        itemShoppingList,
                        itemProfile,
                        new DividerDrawerItem(),
                        itemRecipe,
                        itemDiscover
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                context.startActivity(new Intent(context, FoodcacheActivity.class));
                                selectedItem = 1;
                                return true;
                            case 2:
                                context.startActivity(new Intent(context, ShoppingListActivity.class));
                                selectedItem = 2;
                                return true;
                            case 3:
                                context.startActivity(new Intent(context, ProfileActivity.class));
                                selectedItem = 4;
                                return true;
                            case 5:
                                context.startActivity(new Intent(context, RecipeActivity.class));
                                selectedItem = 3;
                                return true;
                            case 6:
                                context.startActivity(new Intent(context, DiscoverRecipeActivity.class));
                                selectedItem = 5;
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

    // Crop square image, taken from https://stackoverflow.com/questions/26263660/crop-image-to-square-android
    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }
}
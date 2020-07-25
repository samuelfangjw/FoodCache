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

import com.breakfastseta.foodcache.family.FamilyActivity;
import com.breakfastseta.foodcache.inventory.FoodcacheActivity;
import com.breakfastseta.foodcache.profile.Profile;
import com.breakfastseta.foodcache.profile.ProfileActivity;
import com.breakfastseta.foodcache.recipe.RecipeActivity;
import com.breakfastseta.foodcache.recipe.discover.DiscoverRecipeActivity;
import com.breakfastseta.foodcache.recipe.recommend.RecommendActivity;
import com.breakfastseta.foodcache.shoppinglist.ShoppingListActivity;
import com.breakfastseta.foodcache.social.SocialFriendsActivity;
import com.breakfastseta.foodcache.social.SocialMainActivity;
import com.bumptech.glide.Glide;
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

    public static int selectedItem = 1;
    private static final String TAG = "Util";

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
        PrimaryDrawerItem itemShoppingList = new PrimaryDrawerItem().withIdentifier(2).withName("Shopping List");
        PrimaryDrawerItem itemRecipe = new PrimaryDrawerItem().withIdentifier(3).withName("RecipeCache");
        PrimaryDrawerItem itemProfile = new PrimaryDrawerItem().withIdentifier(4).withName("Profile");
        PrimaryDrawerItem itemDiscover = new PrimaryDrawerItem().withIdentifier(5).withName("Discover Recipes");
        PrimaryDrawerItem itemRecommend = new PrimaryDrawerItem().withIdentifier(6).withName("Find a Recipe");
        PrimaryDrawerItem itemSocial = new PrimaryDrawerItem().withIdentifier(10).withName("Social");
        PrimaryDrawerItem itemFriends = new PrimaryDrawerItem().withIdentifier(9).withName("Friends");
        PrimaryDrawerItem itemFamily = new PrimaryDrawerItem().withIdentifier(11).withName("Family Sharing");


        Profile profile = App.getProfile();
        Uri uri = profile.getUri();
        String name = profile.getName();
        String email = profile.getEmail();

        ProfileDrawerItem profileItem;
        if (uri != null) {
            profileItem = new ProfileDrawerItem().withName(name).withEmail(email).withIcon(uri);
        } else {
            profileItem = new ProfileDrawerItem().withName(name).withEmail(email);
        }

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
                .addProfiles(profileItem)
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
                        itemDiscover,
                        itemRecommend,
                        new DividerDrawerItem(),
                        itemSocial,
                        itemFriends,
                        new DividerDrawerItem(),
                        itemFamily
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                Intent intent1 = new Intent(context, FoodcacheActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent1);
                                selectedItem = 1;
                                return true;
                            case 2:
                                Intent intent2 = new Intent(context, ShoppingListActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent2);
                                selectedItem = 2;
                                return true;
                            case 3:
                                Intent intent3 = new Intent(context, ProfileActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent3);
                                selectedItem = 4;
                                return true;
                            case 5:
                                Intent intent5 = new Intent(context, RecipeActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent5);
                                selectedItem = 3;
                                return true;
                            case 6:
                                Intent intent6 = new Intent(context, DiscoverRecipeActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent6);
                                selectedItem = 5;
                                return true;
                            case 7:
                                Intent intent7 = new Intent(context, RecommendActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent7);
                                selectedItem = 6;
                                return true;
                            case 9:
                                Intent intent9 = new Intent(context, SocialMainActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent9);
                                selectedItem = 10;
                                return true;
                            case 10:
                                Intent intent10 = new Intent(context, SocialFriendsActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent10);
                                selectedItem = 9;
                                return true;
                            case 12:
                                Intent intent12 = new Intent(context, FamilyActivity.class);
                                ((Activity) context).finish();
                                context.startActivity(intent12);
                                selectedItem = 11;
                                return true;
                        }
                        return false;
                    }
                })
                .build();
        return result;
    }

    // Helper class for formatting quantity based on units
    public static String formatQuantity(double quantity, String unit) {
        DecimalFormat intFormat = new DecimalFormat("#");
        DecimalFormat twoDPFormat = new DecimalFormat("0.00");

        switch (unit) {
            case "g":
            case "ml":
                return intFormat.format(quantity) + " " + unit;
            case "kg":
            case "Cups":
                return twoDPFormat.format(quantity) + " " + unit;
            case "Items":
                return intFormat.format(quantity);
            default:
                return "" + quantity + " " + unit;
        }
    }

    // Helper class for formatting quantity based on units
    public static String formatQuantityNumber(double quantity, String unit) {
        DecimalFormat intFormat = new DecimalFormat("#");
        DecimalFormat twoDPFormat = new DecimalFormat("0.00");

        if (quantity < 0) {
            quantity = 0;
        }

        switch (unit) {
            case "g":
            case "ml":
            case "Items":
                return intFormat.format(quantity);
            case "kg":
            case "Cups":
                return twoDPFormat.format(quantity);
            default:
                return "" + quantity;
        }
    }

    // Crop square image, adapted from https://stackoverflow.com/questions/26263660/crop-image-to-square-android
    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

}
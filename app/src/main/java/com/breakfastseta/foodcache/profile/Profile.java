package com.breakfastseta.foodcache.profile;

import android.net.Uri;

import java.util.ArrayList;

public class Profile {
    String name;
    String username;
    String email;
    String UID;
    String familyUID;
    String photoURL;
    ArrayList<String> friends = new ArrayList<>();
    int recipeCount;

    public Profile() {
        // empty constructor
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getUID() {
        return UID;
    }

    public String getFamilyUID() {
        return familyUID;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setFamilyUID(String familyUID) {
        this.familyUID = familyUID;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public Uri getUri() {
        if (photoURL == null) {
            return null;
        } else {
            return Uri.parse(photoURL);
        }
    }

    public int getFriendsCount() {
        return friends.size();
    }

    public int getRecipeCount() {
        return recipeCount;
    }

    public void setRecipeCount(int recipeCount) {
        this.recipeCount = recipeCount;
    }
}
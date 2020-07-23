package com.breakfastseta.foodcache.profile;

import android.net.Uri;

import com.breakfastseta.foodcache.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Profile {
    String name;
    String username;
    String email;
    String UID;
    String familyUID;
    String photoURL;
    ArrayList<String> friends = new ArrayList<>();
    Long recipeCount = 0L;
    Boolean useFamilySharing = false;
    Map<String, Long> recipesPrepared = new HashMap<>();
    ArrayList<String> friendRequests = new ArrayList<>();

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

    public Long getRecipeCount() {
        return recipeCount;
    }

    public void setRecipeCount(Long recipeCount) {
        this.recipeCount = recipeCount;
    }

    public Boolean getUseFamilySharing() {
        return useFamilySharing;
    }

    public void setUseFamilySharing(Boolean useFamilySharing) {
        this.useFamilySharing = useFamilySharing;
    }

    public Map<String, Long> getRecipesPrepared() {
        return recipesPrepared;
    }

    public void setRecipesPrepared(Map<String, Long> recipesPrepared) {
        this.recipesPrepared = recipesPrepared;
    }

    public void addRecipePrepared(String recipePath) {
        if (recipesPrepared.containsKey(recipePath)) {
            Long count = recipesPrepared.get(recipePath);
            count++;
            recipesPrepared.put(recipePath, count);
        } else {
            recipesPrepared.put(recipePath, 1L);
        }
    }

    public void addRecipeCount() {
        recipeCount++;
        App.getProfileRef().update("recipeCount", recipeCount);
    }

    public void minusRecipeCount() {
        if (recipeCount > 0) {
            recipeCount--;
            App.getProfileRef().update("recipeCount", recipeCount);
        }
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<String> friendRequests) {
        this.friendRequests = friendRequests;
    }
}
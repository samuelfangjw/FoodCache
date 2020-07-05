package com.breakfastseta.foodcache;

import android.net.Uri;

import java.util.ArrayList;

public class Recipe {
    private String name;
    private String author;
    private ArrayList<Ingredient> ingredients;
    private String steps;
    private Uri photo = null;

    public Recipe() {
        //empty constructor required for Firestore
    }

    public Recipe(String name, String author, ArrayList<Ingredient> ingredients, String steps) {
        this.name = name;
        this.author = author;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getAuthor() {
        return author;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }
}

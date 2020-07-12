package com.breakfastseta.foodcache.recipe;

import java.util.ArrayList;

public class Recipe {
    private String name;
    private String author;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<String> steps;
    private String photo = null;
    private String cuisine;
    private String owner;
    private boolean isPublic;
    private ArrayList<String> viewers;
    private String description;

    public Recipe() {
        //empty constructor required for Firestore
    }

    public Recipe(String name, String author, ArrayList<Ingredient> ingredients,
                  ArrayList<String> steps, String photo, String cuisine, String owner, boolean isPublic, ArrayList<String> viewers, String description) {
        this.name = name;
        this.author = author;
        this.ingredients = ingredients;
        this.steps = steps;
        this.photo = photo;
        this.cuisine = cuisine;
        this.owner = owner;
        this.isPublic = isPublic;
        this.viewers = viewers;
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getCuisine() { return cuisine; }

    public String getOwner() {
        return owner;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public ArrayList<String> getViewers() {
        return viewers;
    }

    public String getDescription() { return description; }
}

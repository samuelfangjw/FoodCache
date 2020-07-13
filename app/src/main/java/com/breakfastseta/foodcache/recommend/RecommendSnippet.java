package com.breakfastseta.foodcache.recommend;

import com.breakfastseta.foodcache.recipe.Ingredient;

import java.util.ArrayList;

public class RecommendSnippet {

    private String name;
    private String image;
    private String path;
    private String cuisine;
    private ArrayList<Ingredient> ingredients;

    public RecommendSnippet(String name, String image, String path, String cuisine, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.image = image;
        this.path = path;
        this.cuisine = cuisine;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getPath() {
        return path;
    }

    public String getCuisine() {
        return cuisine;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }
}

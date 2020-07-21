package com.breakfastseta.foodcache.recipe.recommend;

import com.breakfastseta.foodcache.recipe.Ingredient;

import java.util.ArrayList;

public class RecommendSnippet {

    private String name;
    private String image;
    private String path;
    private String cuisine;
    private ArrayList<Ingredient> ingredients;
    private double recipeScore;

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

    public double getRecipeScore() {
        return recipeScore;
    }

    public void setRecipeScore(double recipeScore) {
        this.recipeScore = recipeScore;
    }

    @Override
    public String toString() {
        return "{name='" + name + ", recipeScore=" + recipeScore + '}';
    }
}

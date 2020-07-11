package com.breakfastseta.foodcache.recipe;

public class RecipeSnippet {
    private String name;
    private String image;
    private String path;

    public RecipeSnippet() {

    }

    public RecipeSnippet(String name, String image, String path) {
        this.name = name;
        this.image = image;
        this.path = path;
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
}

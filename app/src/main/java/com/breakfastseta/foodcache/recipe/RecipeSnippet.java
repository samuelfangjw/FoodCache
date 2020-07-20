package com.breakfastseta.foodcache.recipe;

import com.breakfastseta.foodcache.App;

public class RecipeSnippet {
    private String name;
    private String image;
    private String description;
    private String path;
    private String uid;

    public RecipeSnippet() {

    }

    public RecipeSnippet(String name, String image, String path, String description) {
        this.name = name;
        this.image = image;
        this.path = path;
        this.uid = App.getUID();
        this.description = description;
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

    public String getUid() {
        return uid;
    }

    public String getDescription() {
        return description;
    }
}

package com.breakfastseta.foodcache.recipe.discover;

public class DiscoverSnippet {

    private String name;
    private String image;
    private String path;
    private String cuisine;
    private String description;

    public DiscoverSnippet(String name, String image, String path, String cuisine, String description) {
        this.name = name;
        this.image = image;
        this.path = path;
        this.cuisine = cuisine;
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

    public String getCuisine() {
        return cuisine;
    }

    public String getDescription() {
        return description;
    }
}

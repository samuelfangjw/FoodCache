package com.breakfastseta.foodcache.recipe;

public class DiscoverSnippet {

    private String name;
    private String image;
    private String path;
    private String cuisine;

    public DiscoverSnippet(String name, String image, String path, String cuisine) {
        this.name = name;
        this.image = image;
        this.path = path;
        this.cuisine = cuisine;
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
}

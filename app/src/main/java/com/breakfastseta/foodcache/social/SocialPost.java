package com.breakfastseta.foodcache.social;

public class SocialPost {

    private SocialPostType type;
    private String name;
    private String desc;
    private double noItems;
    private String units;
    private String downloadURL;

    public SocialPost() {
        //empty constructor needed
    }

    public SocialPost(SocialPostType type, String name, String desc, double noItems, String units) {
        if (type == SocialPostType.REQUESTPOST) {
            this.type = type;
            this.name = name;
            this.desc = desc;
            this.noItems = noItems;
            this.units = units;
            this.downloadURL = null;
        }
    }

    public SocialPost(SocialPostType type, String name, String desc, String downloadURL) {
        if (type == SocialPostType.BLOGPOST) {
            this.type = type;
            this.name = name;
            this.desc = desc;
            this.noItems = 0;
            this.units = null;
            this.downloadURL = downloadURL;

        }
    }

    public SocialPostType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public double getNoItems() {
        return noItems;
    }

    public String getUnits() {
        return units;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
}

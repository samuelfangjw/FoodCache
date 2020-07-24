package com.breakfastseta.foodcache.social;

import com.breakfastseta.foodcache.App;

public class SocialPostSnippet {
    private String date;
    private String time;
    private String uID;
    private String path;
    private SocialPostType type;

    public SocialPostSnippet() {

    }

    public SocialPostSnippet(String date, String time, String path, SocialPostType type) {
        this.date = date;
        this.time = time;
        this.uID = App.getUID();
        this.path = path;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getuID() {
        return uID;
    }

    public String getPath() {
        return path;
    }

    public SocialPostType getType() {
        return type;
    }
}

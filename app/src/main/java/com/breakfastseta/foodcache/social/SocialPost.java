package com.breakfastseta.foodcache.social;

import android.net.Uri;

public class SocialPost {

    private SocialPostType type;
    private String name;
    private String desc;
    private double noItems;
    private String units;
    private String downloadURL;
    private String userName;
    private String date;
    private String time;
    private Uri profileImage;
    private String uID;


    public SocialPost() {
        //empty constructor needed
    }

    public SocialPost(SocialPostType type, String name, String desc, double noItems, String units, String userName
    , String date, String time, Uri profileImage, String uID) {
        if (type == SocialPostType.REQUESTPOST) {
            this.type = type;
            this.name = name;
            this.desc = desc;
            this.noItems = noItems;
            this.units = units;
            this.downloadURL = null;
            this.userName = userName;
            this.date = date;
            this.time = time;
            this.profileImage = profileImage;
            this.uID = uID;
        }
    }

    public SocialPost(SocialPostType type, String name, String desc, String downloadURL, String userName
            , String date, String time, Uri profileImage, String uID) {
        if (type == SocialPostType.BLOGPOST) {
            this.type = type;
            this.name = name;
            this.desc = desc;
            this.noItems = 0;
            this.units = null;
            this.downloadURL = downloadURL;
            this.userName = userName;
            this.date = date;
            this.time = time;
            this.profileImage = profileImage;
            this.uID = uID;
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

    public String getUserName() {
        return userName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Uri getProfileImage() {
        return profileImage;
    }

    public String getuID() {
        return uID;
    }
}

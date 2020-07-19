package com.breakfastseta.foodcache.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Family {
    String name;
    String nameLowercase;
    String ownerUID;
    ArrayList<String> members = new ArrayList<>();
    Map<String, Boolean> memberStatus = new HashMap<>();

    public Family() {

    }

    public Family(String name, String ownerUID) {
        this.name = name;
        this.nameLowercase = name.toLowerCase();
        this.ownerUID = ownerUID;
        members.add(ownerUID);
        memberStatus.put(ownerUID, true);
    }

    public String getName() {
        return name;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public Map<String, Boolean> getMemberStatus() {
        return memberStatus;
    }

    public String getNameLowercase() {
        return nameLowercase;
    }
}

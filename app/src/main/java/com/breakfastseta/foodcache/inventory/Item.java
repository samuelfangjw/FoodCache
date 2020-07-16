package com.breakfastseta.foodcache.inventory;

import com.breakfastseta.foodcache.Inventory;
import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Item {
    private String ingredient;
    private double quantity;
    private Timestamp dateTimestamp;
    private String units;
    private String location;
    private Map<String, Double> expiryMap = new HashMap<>();

    public Item() {
        //empty constructor required for Firestore
    }

    public Item(String ingredient, double quantity, Timestamp dateTimestamp, String units, String location) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.dateTimestamp = dateTimestamp;
        this.units = units;
        this.location = location;
        expiryMap.put(dateTimestamp.toString(), quantity);
    }

    public String getIngredient() {
        return ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public Timestamp getDateTimestamp() {
        return dateTimestamp;
    }

    public String getUnits() {
        return units;
    }

    public String getLocation() {
        return location;
    }

    public Map<String, Double> getExpiryMap() {
        return expiryMap;
    }

    public void setExpiryMap(Map<String, Double> expiryMap) {
        this.expiryMap = expiryMap;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    //calculates expiry from expiryMap
    public void recalculateExpiry() {
        TreeMap<Date, Double> tree = new TreeMap<>();
        for (String s : expiryMap.keySet()) {
            tree.put(Inventory.stringToTimestamp(s).toDate(), expiryMap.get(s));
        }

        dateTimestamp = new Timestamp(tree.firstKey());
    }

    public Item makeCopy() {
        Item newItem = new Item(ingredient, quantity, dateTimestamp, units, location);
        newItem.setExpiryMap(this.expiryMap);
        return newItem;
    }
}

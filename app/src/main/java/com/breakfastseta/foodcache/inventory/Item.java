package com.breakfastseta.foodcache.inventory;

import com.google.firebase.Timestamp;

public class Item {
    private String ingredient;
    private double quantity;
    private Timestamp dateTimestamp;
    private String units;

    public Item() {
        //empty constructor required for Firestore
    }

    public Item(String ingredient, double quantity, Timestamp dateTimestamp, String units) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.dateTimestamp = dateTimestamp;
        this.units = units;
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
}

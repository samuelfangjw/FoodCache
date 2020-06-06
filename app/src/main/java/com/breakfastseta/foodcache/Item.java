package com.breakfastseta.foodcache;

import com.google.firebase.Timestamp;

public class Item {
    private String ingredient;
    private int quantity;
    private Timestamp dateTimestamp;

    public Item() {
        //empty constructor required for Firestore
    }

    public Item(String ingredient, int quantity, Timestamp dateTimestamp) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.dateTimestamp = dateTimestamp;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public Timestamp getDateTimestamp() {
        return dateTimestamp;
    }
}

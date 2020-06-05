package com.breakfastseta.foodcache;

public class Item {
    private String ingredient;
    private int quantity;

    public Item() {
        //empty constructor required for Firestore
    }

    public Item(String ingredient, int quantity) {
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public String getIngredient() {
        return ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

}

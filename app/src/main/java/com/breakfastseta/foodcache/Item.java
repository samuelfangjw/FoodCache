package com.breakfastseta.foodcache;

public class Item {
    private String ingredient;
    private String quantity;

    public Item() {
        //empty constructor required for Firestore
    }

    public Item(String ingredient, String quantity) {
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    public String getIngredient() {
        return ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

}

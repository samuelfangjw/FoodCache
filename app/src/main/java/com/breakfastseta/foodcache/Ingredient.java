package com.breakfastseta.foodcache;

public class Ingredient {
    private String name;
    private int quantity;
    private String units;

    public Ingredient() {
        //empty constructor required for Firestore
    }

    public Ingredient(String name, int quantity, String units) {
        this.name = name;
        this.quantity = quantity;
        this.units = units;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnits() {
        return units;
    }

    public String getName() {
        return name;
    }

}

package com.breakfastseta.foodcache.recipe;

public class Ingredient {
    private String name;
    private double quantity;
    private String units;

    public Ingredient() {
        //empty constructor required for Firestore
    }

    public Ingredient(String name, double quantity, String units) {
        this.name = name;
        this.quantity = quantity;
        this.units = units;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnits() {
        return units;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + quantity + " " + units;
    }
}

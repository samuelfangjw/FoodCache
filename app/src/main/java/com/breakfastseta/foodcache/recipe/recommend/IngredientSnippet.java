package com.breakfastseta.foodcache.recipe.recommend;

import androidx.annotation.NonNull;

public class IngredientSnippet {
    private String name;
    private double quantity;
    private String units;
    private long timeLeft;

    public IngredientSnippet(String name, double quantity, String units, long timeLeft) {
        this.name = name;
        this.quantity = quantity;
        this.units = units;
        this.timeLeft = timeLeft;
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

    public long getTimeLeft() {
        return timeLeft;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + quantity + " " + units + " " + timeLeft;
    }
}

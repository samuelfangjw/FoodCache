package com.breakfastseta.foodcache.shoppinglist;

public class ShoppingListItem {

    private String itemName;
    private String description;
    private double noItems;
    private String units;

    public ShoppingListItem() {
        //empty constructor needed
    }

    public ShoppingListItem(String itemName, String description, double noItems, String units) {
        this.itemName = itemName;
        this.description = description;
        this.noItems = noItems;
        this.units = units;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getNoItems() {
        return noItems;
    }

    public String getUnits() {
        return units;
    }
}

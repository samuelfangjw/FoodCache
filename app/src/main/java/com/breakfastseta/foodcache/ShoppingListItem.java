package com.breakfastseta.foodcache;

public class ShoppingListItem {

    private String itemName;
    private String description;
    private int noItems;
    private String units;

    public ShoppingListItem() {
        //empty constructor needed
    }

    public ShoppingListItem(String itemName, String description, int noItems, String units) {
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

    public int getNoItems() {
        return noItems;
    }

    public String getUnits() {
        return units;
    }
}

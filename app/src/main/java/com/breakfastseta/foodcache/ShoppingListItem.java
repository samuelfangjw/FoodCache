package com.breakfastseta.foodcache;

public class ShoppingListItem {

    private String itemName;
    private String description;
    private int noItems;

    public ShoppingListItem() {
        //empty constructor needed
    }

    public ShoppingListItem(String itemName, String description, int noItems) {
        this.itemName = itemName;
        this.description = description;
        this.noItems = noItems;
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
}

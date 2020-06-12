package com.breakfastseta.foodcache;

public class ShoppingListItem {

    private String itemName;
    private String description;
    private int noItems;
    private int priority;

    public ShoppingListItem() {
        //empty constructor needed
    }

    public ShoppingListItem(String itemName, String description, int noItems, int priority) {
        this.itemName = itemName;
        this.description = description;
        this.priority = priority;
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

    public int getPriority() {
        return priority;
    }
}

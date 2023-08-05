package com.parim.event;

import com.parim.model.ItemOfClient;

import java.util.ArrayList;

public class ItemEvent implements FormEvent {
    private ArrayList<ItemOfClient> availableItems, allItems;
    public ItemEvent(){}

    public ItemEvent(ArrayList<ItemOfClient> availableItems, ArrayList<ItemOfClient> allItems) {
        this.availableItems = availableItems;
        this.allItems = allItems;
    }

    public ArrayList<ItemOfClient> getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(ArrayList<ItemOfClient> availableItems) {
        this.availableItems = availableItems;
    }

    public ArrayList<ItemOfClient> getAllItems() {
        return allItems;
    }

    public void setAllItems(ArrayList<ItemOfClient> allItems) {
        this.allItems = allItems;
    }
}

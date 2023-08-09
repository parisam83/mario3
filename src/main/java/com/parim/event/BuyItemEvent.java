package com.parim.event;

public class BuyItemEvent implements FormEvent {
    private String item;
    public BuyItemEvent(){}

    public BuyItemEvent(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}


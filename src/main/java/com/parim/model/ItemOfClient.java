package com.parim.model;

public class ItemOfClient {
    private String name, cost;

    public ItemOfClient() {}

    public ItemOfClient(String name, String cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}

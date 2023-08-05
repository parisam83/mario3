package com.parim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.parim.event.ItemEvent;
import com.parim.loader.ConfigLoader;
import com.parim.model.gameObjects.Item;
import com.parim.model.gameObjects.damages.DamageBomb;
import com.parim.model.gameObjects.damages.Hammer;
import com.parim.model.gameObjects.damages.SpeedBomb;
import com.parim.model.gameObjects.potions.HealthPotion;
import com.parim.model.gameObjects.potions.InvisibilityPotion;
import com.parim.model.gameObjects.potions.SpeedPotion;
import com.parim.model.gameObjects.sword.Sword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class User {
    private String username, password;
    private int coins = 100, diamond;
    @JsonIgnore
    private ArrayList<Item> items = new ArrayList<Item>(){
        {
            add(new Sword());
            add(new HealthPotion());
            add(new InvisibilityPotion());
            add(new SpeedPotion());
            add(new DamageBomb());
            add(new Hammer());
            add(new SpeedBomb());
        }
    };
    private ArrayList<ItemOfClient> allItems = new ArrayList<>(), availableItems = new ArrayList<>();
    public User(){}
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public ItemEvent GetItems(){
        allItems.clear();
        availableItems.clear();
        for (Item item : items){
            ItemOfClient newItem = new ItemOfClient(item.getClass().getSimpleName(), String.valueOf(item.getCOST()));
            if (checkItem(item)) availableItems.add(newItem);
            allItems.add(newItem);
        }
        return new ItemEvent(availableItems, allItems);
    }

    private boolean checkItem(Item item){
        System.out.println(this);
        System.out.println(item);
        if (item.isPAYS_WITH_DIAMOND() && item.getCOST() > diamond * ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate")) return false;
        if (item.getCOST() > coins && item.getCOST() > diamond * ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate")) return false;
        System.out.println("coin enough");
        if (item.getMAX_AMOUNT() != -1 && item.getMAX_AMOUNT() < item.getMaxAmount()) return false;
        System.out.println("in time");
        System.out.println();
        if (item.getIN_TIME() != -1 && item.getLastTimeBought() != null && (new Date().before(new Date(item.getLastTimeBought().getTime() + item.getIN_TIME())))) return false;
        System.out.println("onr user");
        if (item.getMAX_AMOUNT_BY_ONE_USER() != -1 && item.getMAX_AMOUNT_BY_ONE_USER() < item.getMaxAmountByOneUser()) return false;
        System.out.println("min level");
        if (item.getMIN_LEVEL() > item.getMinLevel()) return false;
        System.out.println("done");
        return true;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", coins=" + coins +
                ", diamond=" + diamond +
                ", items=" + items +
                '}';
    }
}

package com.parim.model;

import java.util.ArrayList;

public class User {
    private String username, password;
    private int coins = 100, diamond;
    private final ArrayList<String> chatList = new ArrayList<>();
    public User(){}
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public void reduceCoins(int amount){
        coins -= amount;
    }
    public void reduceDiamonds(int amount){
        diamond -= amount;
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

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public ArrayList<String> getChatList() {
        return chatList;
    }
}

package com.parim.model;

import com.parim.event.notification.NotificationEvent;

import java.util.ArrayList;

public class User {
    private String username, password;
    private int coins = 100, diamond = 1, level = 1;
    private final ArrayList<String> chatList = new ArrayList<>(), blockedUsernames = new ArrayList<>();
    private final ArrayList<NotificationEvent> notifications = new ArrayList<>();
    private ArrayList<String> boughtItems = new ArrayList<>(), havingItems = new ArrayList<>();
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
    public void unblockUser(String username){
        if (blockedUsernames.contains(username))
            blockedUsernames.remove(username);
    }
    public void blockUser(String username){
        if (!blockedUsernames.contains(username))
            blockedUsernames.add(username);
    }
    public void addChatList(String username){
        chatList.add(username);
    }
    public void removeChatList(String username){
        chatList.remove(username);
    }
    public void addNotification(NotificationEvent notificationEvent){
        notifications.add(notificationEvent);
    }
    public void removeNotification(NotificationEvent notificationEvent){
        for (NotificationEvent notification : notifications)
            if (notification.getMessage().equals(notificationEvent.getMessage())
             && notification.getTitle().equals(notificationEvent.getTitle())
             && notification.getType().equals(notificationEvent.getType())) {
                notifications.remove(notification);
                return;
            }
    }
    public void addBoughtItem(String item){
        addHavingItem(item);
        boughtItems.add(item);
    }
    private void addHavingItem(String item){
        havingItems.add(item);
    }
    public void removeHavingItem(String item){
        havingItems.remove(item);
    }
    public int numberOfBought(String item){
        int cnt = 0;
        for (String boughtItem : boughtItems)
            if (boughtItem.equals(item))
                cnt++;
        return cnt;
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

    public ArrayList<String> getBlockedUsernames() {
        return blockedUsernames;
    }

    public ArrayList<NotificationEvent> getNotifications() {
        return notifications;
    }

    public ArrayList<String> getBoughtItems() {
        return boughtItems;
    }

    public void setBoughtItems(ArrayList<String> boughtItems) {
        this.boughtItems = boughtItems;
    }

    public ArrayList<String> getHavingItems() {
        return havingItems;
    }

    public void setHavingItems(ArrayList<String> havingItems) {
        this.havingItems = havingItems;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}

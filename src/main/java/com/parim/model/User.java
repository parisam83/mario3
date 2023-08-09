package com.parim.model;

import com.parim.event.notification.NotificationEvent;

import java.util.ArrayList;

public class User {
    private String username, password;
    private int coins = 100, diamond;
    private final ArrayList<String> chatList = new ArrayList<>(), blockedUsernames = new ArrayList<>();
    private final ArrayList<NotificationEvent> notifications = new ArrayList<>();
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
}

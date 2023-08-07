package com.parim;

import com.parim.access.UserAccess;
import com.parim.event.*;
import com.parim.loader.ConfigLoader;
import com.parim.model.Chat;
import com.parim.model.ItemOfClient;
import com.parim.model.User;
import com.parim.model.gameObjects.Item;
import com.parim.model.gameObjects.damages.DamageBomb;
import com.parim.model.gameObjects.damages.Hammer;
import com.parim.model.gameObjects.damages.SpeedBomb;
import com.parim.model.gameObjects.potions.HealthPotion;
import com.parim.model.gameObjects.potions.InvisibilityPotion;
import com.parim.model.gameObjects.potions.SpeedPotion;
import com.parim.model.gameObjects.sword.Sword;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class Server {
    private ServerSocket serverSocket;
    private UserAccess userAccess;
    private ArrayList<ClientThread> openClientThreads = new ArrayList<>(), allClientThreads = new ArrayList<>();
    private ArrayList<Chat> allChats = new ArrayList<>();
    private final static ArrayList<Item> ITEMS = new ArrayList<Item>(){
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
    public Server() throws IOException {
        serverSocket = new ServerSocket(9000);
        userAccess = new UserAccess();
        runServer();
    }
    private void runServer() throws IOException {
        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            ClientThread clientThread = new ClientThread(socket, this);
            openClientThreads.add(clientThread);
            allClientThreads.add(clientThread);
            clientThread.start();
        }
    }
    public static void main(String[] args) throws IOException {
        new Server();
    }

    public void receivedClientClosedEvent(ClientThread clientThread) {
        userAccess.update(clientThread.getUser());
        openClientThreads.remove(clientThread);
    }
    public void receivedUserRegisterEvent(UserEvent userEvent, ClientThread clientThread) {
        String username = userEvent.getUser().getUsername(), password = userEvent.getUser().getPassword();
        if (userAccess.findUser(username, password) == null) {
            clientThread.setUser(userAccess.add(username, password));
            clientThread.sendRegisterResult("UserRegisterSuccessful");
        }
        else clientThread.sendRegisterResult("UserRegisterUnsuccessful");
    }
    public void receivedUserLoginEvent(UserEvent userEvent, ClientThread clientThread) {
        String username = userEvent.getUser().getUsername(), password = userEvent.getUser().getPassword();
        if (userAccess.findUser(username, password) != null) {
            clientThread.setUser(userAccess.findUser(username, password));
            clientThread.sendLoginResult("UserLoginSuccessful");
        }
        else clientThread.sendLoginResult("UserLoginUnsuccessful");
    }
    public void receivedItemEvent(ClientThread clientThread){
        ItemEvent itemEvent = getItemEvent(clientThread.getUser());
        ArrayList<ItemOfClient> availableItems = itemEvent.getAvailableItems();
        clientThread.sendItemEvent(itemEvent);
        if (availableItems.size() > 1){
            ItemEvent comboItemEvent = new ItemEvent(new ArrayList<ItemOfClient>(){{
                add(availableItems.get(0));
                add(availableItems.get(1));
            }}, null);
            clientThread.sendComboItemEvent(comboItemEvent);
        }
    }
    public void receivedBuyItemEvent(BuyItemEvent buyItemEvent, ClientThread clientThread){
        String name = buyItemEvent.getItem();
        Item item = findItem(name);
        User user = clientThread.getUser();
        if (item.getCOST() > user.getCoins() && item.getCOST() <= user.getDiamond() * ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate")){
            buyItemEvent.setHasOptionPain(true);
        }
        else{
            if (item.getCOST() <= user.getCoins())
                user.reduceCoins(-item.getCOST());
            else if (item.isPAYS_WITH_DIAMOND() && item.getCOST() <= user.getDiamond() * ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate"))
                user.reduceDiamonds(item.getCOST()/ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate"));
            // TODO: else if (item)
            buyItemEvent.setVerdict(true);
        }
        clientThread.sendBuyItemEvent(buyItemEvent);
    }
    public void receivedChatListRequest(ClientThread clientThread){
        ArrayList<String> chatList = new ArrayList<>();
        for (Chat chat : allChats) {
            if (chat.getUsername1().equals(clientThread.getUser())) chatList.add(chat.getUsername2());
            if (chat.getUsername2().equals(clientThread.getUser())) chatList.add(chat.getUsername1());
        }
        clientThread.sendChatListRequest(chatList);
    }
    public void receivedMessageEvent(MessageEvent messageEvent, ClientThread clientThread){
        Chat chat = findChat(messageEvent.getChat().getUsername1(), clientThread.getUser().getUsername());
        System.out.println("sender : " + chat.getUsername2() +"\nreceiver: " + chat.getUsername1());
        System.out.println(chat.getMessages() + "\n");
        clientThread.sendMessageEvent(chat);
    }
    public void receivedSendMessageEvent(SendMessageEvent sendMessageEvent, ClientThread clientThread){
        Chat chat = findChat(sendMessageEvent.getSender(), sendMessageEvent.getReceiver());
        chat.addMessage(sendMessageEvent.getSender(), sendMessageEvent.getMessage());
        clientThread.sendMessageEvent(chat);
    }
    public boolean checkItem(Item item, User user){
        if (item.isPAYS_WITH_DIAMOND() && item.getCOST() > user.getDiamond() * ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate")) return false;
        if (item.getCOST() > user.getCoins() && item.getCOST() > user.getDiamond() * ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate")) return false;
        if (item.getMAX_AMOUNT() != -1 && item.getMAX_AMOUNT() < item.getMaxAmount()) return false;
        if (item.getIN_TIME() != -1 && item.getLastTimeBought() != null && (new Date().before(new Date(item.getLastTimeBought().getTime() + item.getIN_TIME())))) return false;
        if (item.getMAX_AMOUNT_BY_ONE_USER() != -1 && item.getMAX_AMOUNT_BY_ONE_USER() < item.getMaxAmountByOneUser()) return false;
        if (item.getMIN_LEVEL() > item.getMinLevel()) return false;
        return true;
    }
    private Item findItem(String name){
        for (Item item : ITEMS)
            if (item.getClass().getSimpleName().equals(name))
                return item;
        throw new RuntimeException();
    }
    private Chat findChat(String username1, String username2){
        for (Chat chat : allChats)
            if ((chat.getUsername1().equals(username1) && chat.getUsername2().equals(username2))
            ||  (chat.getUsername1().equals(username2) && chat.getUsername2().equals(username1)))
                return chat;
        return new Chat(username1, username2);
    }
    private ClientThread findClientThread(String username){
        for (ClientThread clientThread : allClientThreads)
            if (clientThread.getUser().getUsername().equals(username))
                return clientThread;
        throw new RuntimeException();
    }
    public ItemEvent getItemEvent(User user){
        ArrayList<ItemOfClient> allItems = new ArrayList<>(), availableItems = new ArrayList<>();
        for (Item item : Server.getITEMS()){
            String currency = getCurrency(item);
            ItemOfClient newItem = new ItemOfClient(item.getClass().getSimpleName(),
                    String.valueOf(item.getCOST()) + currency);
            if (checkItem(item, user)) availableItems.add(newItem);
            allItems.add(newItem);
        }
        return new ItemEvent(availableItems, allItems);
    }
    private String getCurrency(Item item){
        if (item.isPAYS_WITH_DIAMOND()) return "D";
        return "C";
    }

    public static ArrayList<Item> getITEMS() {
        return ITEMS;
    }
}
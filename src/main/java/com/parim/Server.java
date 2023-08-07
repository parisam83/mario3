package com.parim;

import com.parim.access.UserAccess;
import com.parim.event.*;
import com.parim.event.chat.block.BlockUserEvent;
import com.parim.event.chat.block.UnblockUserEvent;
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
        clientThread.sendChatListRequest(clientThread.getUser().getChatList());
    }
    public void receivedMessageEvent(MessageEvent messageEvent, ClientThread clientThread){
        if (userAccess.usernameExists(messageEvent.getChat().getUsername1())) {
            Chat chat = findChat(messageEvent.getChat().getUsername1(), clientThread.getUser().getUsername());
            clientThread.sendMessageEvent(chat);
        }
    }
    public void receivedSendMessageEvent(SendMessageEvent sendMessageEvent, ClientThread clientThread){
        Chat chat = findChat(sendMessageEvent.getSender(), sendMessageEvent.getReceiver());
        addChatToUsers(sendMessageEvent.getReceiver(), sendMessageEvent.getSender());
        chat.addMessage(sendMessageEvent.getSender(), sendMessageEvent.getMessage());
        ClientThread receiver = findClientThread(sendMessageEvent.getReceiver());
        if (receiver != null) receiver.sendMessageEvent(chat);
    }
    public void blockUser(BlockUserEvent blockUserEvent, ClientThread clientThread){
        if (userAccess.usernameExists(blockUserEvent.getUsername()))
            clientThread.getUser().blockUser(blockUserEvent.getUsername());
    }
    public void unblockUser(UnblockUserEvent unblockUserEvent, ClientThread clientThread){
        if (userAccess.usernameExists(unblockUserEvent.getUsername()))
            clientThread.getUser().unblockUser(unblockUserEvent.getUsername());
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
        for (Chat chat : allChats) {
            System.out.println(chat.getUsername1() + ", " + chat.getUsername2());
            if ((chat.getUsername1().equals(username1) && chat.getUsername2().equals(username2))
                    || (chat.getUsername1().equals(username2) && chat.getUsername2().equals(username1))) {
                return chat;
            }
        }
        Chat chat = new Chat(username1, username2);
        allChats.add(chat);
        addChatToUsers(username1, username2);
        return chat;
    }
    private void addChatToUsers(String username1, String username2){
        User u1 = userAccess.findUserByUsername(username1), u2 = findClientThread(username2).getUser();
        u1.removeChatList(username2);
        u1.addChatList(username2);

        u2.removeChatList(username1);
        u2.addChatList(username1);
    }
    private ClientThread findClientThread(String username){
        for (ClientThread clientThread : allClientThreads)
            if (clientThread.getUser() != null && clientThread.getUser().getUsername().equals(username))
                return clientThread;
        return null;
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
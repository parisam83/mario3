package com.parim;

import com.parim.event.*;
import com.parim.event.chat.block.BlockUserEvent;
import com.parim.event.chat.block.UnblockUserEvent;
import com.parim.event.room.RoomEvent;
import com.parim.loader.ConfigLoader;
import com.parim.model.Chat;
import com.parim.model.ItemOfClient;
import com.parim.model.Room;
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
    private final ServerSocket serverSocket;
    private final ArrayList<ClientThread> openClientThreads = new ArrayList<>(), allClientThreads = new ArrayList<>();
    private final ArrayList<Chat> allChats = new ArrayList<>();
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<User> users = new ArrayList<>();
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
        serverSocket = new ServerSocket(9001);
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
        openClientThreads.remove(clientThread);
    }
    public void receivedUserRegisterEvent(UserEvent userEvent, ClientThread clientThread) {
        String username = userEvent.getUser().getUsername(), password = userEvent.getUser().getPassword();
        if (findUser(username, password) == null) {
            User user = new User(username, password);
            clientThread.setUser(user);
            users.add(user);
            clientThread.sendRegisterResult("UserRegisterSuccessful");
        }
        else clientThread.sendRegisterResult("UserRegisterUnsuccessful");
    }
    public void receivedUserLoginEvent(UserEvent userEvent, ClientThread clientThread) {
        String username = userEvent.getUser().getUsername(), password = userEvent.getUser().getPassword();
        if (findUser(username, password) != null) {
            clientThread.setUser(findUser(username, password));
            clientThread.sendLoginResult("UserLoginSuccessful");
        }
        else clientThread.sendLoginResult("UserLoginUnsuccessful");
    }
    public void receivedItemEvent(ClientThread clientThread){
        ItemEvent itemEvent = getItemEvent(clientThread.getUser());
        clientThread.sendItemEvent(itemEvent);

        ArrayList<ItemOfClient> availableItems = itemEvent.getAvailableItems();
        if (availableItems.size() > 1){
            ItemEvent comboItemEvent = new ItemEvent(null, new ArrayList<ItemOfClient>(){{
                add(availableItems.get(0));
                add(availableItems.get(1));
            }});

            Item item1 = findItem(availableItems.get(0).getName());
            Item item2 = findItem(availableItems.get(1).getName());
            if (checkComboItem(clientThread.getUser(), item1, item2)){
                comboItemEvent.setAvailableItems(new ArrayList<ItemOfClient>(){{
                    add(availableItems.get(0));
                    add(availableItems.get(1));
                }});
            }
            clientThread.sendComboItemEvent(comboItemEvent);
        }
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
    public boolean checkItem(Item item, User user){
        int diamondConversionRate = ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate");
        if (item.isPAYS_WITH_DIAMOND()){
            if (item.getCOST() > user.getDiamond() * diamondConversionRate) return false;
        }
        else if (item.getCOST() > user.getCoins() + user.getDiamond() * diamondConversionRate) return false;

        if (item.getAMOUNT_LEFT() <= 0) return false;
        if (item.getIN_TIME() != -1 && item.getLAST_TIME_BOUGHT() != null
           && (new Date().before(new Date(item.getLAST_TIME_BOUGHT().getTime() + item.getIN_TIME())))) return false;
        if (item.getMAX_AMOUNT_BY_ONE_USER() >= user.numberOfBought(item.getClass().getSimpleName())) return false;
        if (item.getMIN_LEVEL() > user.getLevel()) return false;
        return true;
    }
    public boolean checkComboItem(User user, Item item1, Item item2){
        int diamondConversionRate = ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate");
        if (item1.isPAYS_WITH_DIAMOND() && item2.isPAYS_WITH_DIAMOND())
            return user.getDiamond() * diamondConversionRate >= item1.getCOST() + item2.getCOST();
        else
            return user.getCoins() + user.getDiamond() * diamondConversionRate >= item1.getCOST() + item2.getCOST();
    }
    public void receivedBuyItemEvent(BuyItemEvent buyItemEvent, ClientThread clientThread){
        String name = buyItemEvent.getItem();
        Item item = findItem(name);
        User user = clientThread.getUser();
        int diamondConversionRate = ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate");

        if (item.isPAYS_WITH_DIAMOND()) user.reduceDiamonds(item.getCOST()/diamondConversionRate);
        else if (user.getCoins() < item.getCOST()){
            user.reduceDiamonds((item.getCOST()-user.getCoins())/diamondConversionRate);
            user.setCoins(0);
        }
        else user.reduceCoins(item.getCOST());

        item.itemBought();
        user.addBoughtItem(name);
        clientThread.sendItemBoughtEvent();
    }
    public void receivedComboBuyItemEvent(ComboBuyItemEvent comboBuyItemEvent, ClientThread clientThread){
        String name1 = comboBuyItemEvent.getItem1(), name2 = comboBuyItemEvent.getItem2();
        Item item1 = findItem(name1), item2 = findItem(name2);
        User user = clientThread.getUser();

        int diamondConversionRate = ConfigLoader.getInstance().getProperty(Integer.class, "Shop.diamondConversionRate");
        if (item1.isPAYS_WITH_DIAMOND() && item2.isPAYS_WITH_DIAMOND())
            user.reduceDiamonds((item1.getCOST() + item2.getCOST())/diamondConversionRate);
        if (item1.isPAYS_WITH_DIAMOND() && !item2.isPAYS_WITH_DIAMOND()){
            user.reduceDiamonds(item1.getCOST()/diamondConversionRate);
            if (user.getCoins() < item2.getCOST()){
                user.reduceDiamonds((item2.getCOST()-user.getCoins())/diamondConversionRate);
                user.setCoins(0);
            }
            else user.reduceCoins(item2.getCOST());
        }
        if (!item1.isPAYS_WITH_DIAMOND() && item2.isPAYS_WITH_DIAMOND()){
            user.reduceDiamonds(item2.getCOST()/diamondConversionRate);
            if (user.getCoins() < item1.getCOST()){
                user.reduceDiamonds((item1.getCOST()-user.getCoins())/diamondConversionRate);
                user.setCoins(0);
            }
            else user.reduceCoins(item1.getCOST());
        }
        if (!item1.isPAYS_WITH_DIAMOND() && !item2.isPAYS_WITH_DIAMOND()){
            int cost = item1.getCOST() + item2.getCOST();
            if (user.getCoins() < cost){
                user.reduceDiamonds((cost-user.getCoins())/diamondConversionRate);
                user.setCoins(0);
            }
            else user.reduceCoins(cost);
        }

        item1.itemBought();
        item2.itemBought();
        user.addBoughtItem(name1);
        user.addBoughtItem(name2);
        clientThread.sendItemBoughtEvent();
    }
    public void receivedChatListRequest(ClientThread clientThread){
        clientThread.sendChatListRequest(clientThread.getUser().getChatList());
    }
    public void receivedMessageEvent(MessageEvent messageEvent, ClientThread clientThread){
        if (usernameExists(messageEvent.getChat().getUsername1())) {
            Chat chat = findChat(messageEvent.getChat().getUsername1(), clientThread.getUser().getUsername());
            clientThread.sendMessageEvent(chat);
        }
    }
    public void blockUser(BlockUserEvent blockUserEvent, ClientThread clientThread){
        if (usernameExists(blockUserEvent.getUsername()))
            clientThread.getUser().blockUser(blockUserEvent.getUsername());
    }
    public void unblockUser(UnblockUserEvent unblockUserEvent, ClientThread clientThread){
        if (usernameExists(unblockUserEvent.getUsername()))
            clientThread.getUser().unblockUser(unblockUserEvent.getUsername());
    }
    public void createNewRoom(RoomEvent roomEvent, ClientThread clientThread){
        Room room = roomEvent.getRoom();
        room.setBoss(clientThread.getUser().getUsername());
        room.setId(String.valueOf(rooms.size()));
        rooms.add(room);
        clientThread.sendRoomEvent(roomEvent);
    }
    public void joinRoom(RoomEvent roomEvent, ClientThread clientThread){
        Room room = findRoom(roomEvent.getRoom().getId(), roomEvent.getRoom().getPassword());
        if (room == null) clientThread.sendRoomEvent(null);
        else {
            room.addMember(clientThread.getUser().getUsername());
            clientThread.sendRoomEvent(roomEvent);
        }
    }
    private Room findRoom(String id, String password){
        for (Room room : rooms)
            if (room.getId().equals(id) && room.getPassword().equals(password))
                return room;
        return null;
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
    private User findUser(String username, String password){
        for (User user : users)
            if (user.getUsername().equals(username) && user.getPassword().equals(password))
                return user;
        return null;
    }
    private boolean usernameExists(String username){
        for (User user : users)
            if (user.getUsername().equals(username))
                return true;
        return false;
    }
    private User findUserByUsername(String username){
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        throw new RuntimeException();
    }
    private void addChatToUsers(String username1, String username2){
        User u1 = findUserByUsername(username1), u2 = findUserByUsername(username2);
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
    private String getCurrency(Item item){
        if (item.isPAYS_WITH_DIAMOND()) return "D";
        return "C";
    }

    public static ArrayList<Item> getITEMS() {
        return ITEMS;
    }
    public void receivedSendMessageEvent(SendMessageEvent sendMessageEvent){
        Chat chat = findChat(sendMessageEvent.getSender(), sendMessageEvent.getReceiver());
        addChatToUsers(sendMessageEvent.getReceiver(), sendMessageEvent.getSender());
        chat.addMessage(sendMessageEvent.getSender(), sendMessageEvent.getMessage());
        ClientThread receiver = findClientThread(sendMessageEvent.getReceiver());
        if (receiver != null) {
            receiver.sendMessageEvent(chat);
            receiver.sendNotificationEvent(sendMessageEvent.getSender(), sendMessageEvent.getMessage(),"Chat");
        }
    }
    public void inviteToRoom(RoomEvent roomEvent, ClientThread clientThread) {
        String sender = clientThread.getUser().getUsername(), receiver = roomEvent.getSomeUser();
        String id = roomEvent.getRoom().getId(), password = roomEvent.getRoom().getPassword();

        receivedSendMessageEvent(new SendMessageEvent(sender, receiver, "You can join our room:\nid = " + id + "\npassword = " + password));
    }
    public void removeFromRoom(RoomEvent roomEvent, ClientThread clientThread) {
        Room room = findRoom(roomEvent.getRoom().getId(), roomEvent.getRoom().getPassword());
        if (room == null) throw new RuntimeException();

        ClientThread receiver = findClientThread(roomEvent.getSomeUser());
        if (receiver != null) {
            room.removeMember(roomEvent.getSomeUser());
            room.addBlockedUser(roomEvent.getSomeUser());
            roomEvent.setSomeUser(clientThread.getUser().getUsername());
            receiver.sendRemoveFromRoom(roomEvent);
        }
    }
    public void addNewAssistant(RoomEvent roomEvent) {
        Room room = findRoom(roomEvent.getRoom().getId(), roomEvent.getRoom().getPassword());
        if (room == null) throw new RuntimeException();

        room.addAssistant(roomEvent.getSomeUser());
    }
    public void runRoomGame(RoomEvent roomEvent) {
        Room room = findRoom(roomEvent.getRoom().getId(), roomEvent.getRoom().getPassword());
        if (room == null) throw new RuntimeException();

        ArrayList<String> allPeople = room.GetAllPeople();
        for (String people : allPeople){
            ClientThread clientThread = findClientThread(people);
            if (clientThread == null) throw new RuntimeException();

            clientThread.sendRunRoomGame(roomEvent);
        }
    }
    public void verdictRunRoom(RoomEvent roomEvent) {
        Room room = findRoom(roomEvent.getRoom().getId(), roomEvent.getRoom().getPassword());
        if (room == null) throw new RuntimeException();

        if (roomEvent.getSomeUser().equals("no")) room.resetGame();
        else{
            room.addYes();
            if (room.readyToStart()) sendStartRoomGame(room);
        }
    }
    private void sendStartRoomGame(Room room) {
        ArrayList<String> allPeople = room.GetAllPeople();
        for (String people : allPeople){
            ClientThread clientThread = findClientThread(people);
            if (clientThread == null) throw new RuntimeException();

            clientThread.sendStartRoomGame(new RoomEvent(room));
        }
    }
}
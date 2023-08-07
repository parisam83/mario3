package com.parim;

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

import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientThread extends Thread {
    private Socket socket;
    private Server server;
    private ConnectToClient connectToClient;
    private User user;
    public ClientThread(Socket socket, Server server){
        this.socket = socket;
        this.server = server;
        connectToClient = new ConnectToClient(socket);
    }

    @Override
    public void run() {
        while (!socket.isClosed()){
            Message clientRespond = connectToClient.receive();
            if (clientRespond == null) break;
            String title = clientRespond.getTitle();
            if (title.equals("ClientClosedEvent")) server.receivedClientClosedEvent(this);
            if (title.equals("UserRegisterEvent")) server.receivedUserRegisterEvent((UserEvent) clientRespond.getFormEvent(), this);
            if (title.equals("UserLoginEvent")) server.receivedUserLoginEvent((UserEvent) clientRespond.getFormEvent(), this);
            if (title.equals("ItemEvent")) server.receivedItemEvent(this);
            if (title.equals("BuyItemEvent")) server.receivedBuyItemEvent((BuyItemEvent) clientRespond.getFormEvent(), this);
            if (title.equals("ChatListRequest")) server.receivedChatListRequest(this);
            if (title.equals("MessageEvent")) server.receivedMessageEvent((MessageEvent) clientRespond.getFormEvent(), this);
            if (title.equals("SendMessageEvent")) server.receivedSendMessageEvent((SendMessageEvent) clientRespond.getFormEvent(), this);
            if (title.equals("ListOfBlockedUsernamesEvent")) sendListOfBlockedUsernames();
            if (title.equals("BlockUserEvent")) blockUser((BlockUserEvent) clientRespond.getFormEvent());
            if (title.equals("UnblockUserEvent")) unblockUser((UnblockUserEvent) clientRespond.getFormEvent());
        }
        server.receivedClientClosedEvent(this);
    }
    private void blockUser(BlockUserEvent blockUserEvent){
        user.blockUser(blockUserEvent.getUsername());
    }
    private void unblockUser(UnblockUserEvent unblockUserEvent){
        user.unblockUser(unblockUserEvent.getUsername());
    }
    private void sendListOfBlockedUsernames(){
        connectToClient.send(new Message("ListOfBlockedUsernamesEvent", new ListOfBlockedUsernamesEvent(user.getBlockedUsernames())));
    }
    public void sendChatListRequest(ArrayList<String> chatList){
        connectToClient.send(new Message("ChatListEvent", new ChatListEvent(chatList)));
    }
    public void sendRegisterResult(String result) {
        connectToClient.send(new Message(result, null));
    }
    public void sendLoginResult(String result) {
        connectToClient.send(new Message(result, null));
    }
    public void sendItemEvent(ItemEvent itemEvent){
        connectToClient.send(new Message("ItemEvent", itemEvent));
    }
    public void sendComboItemEvent(ItemEvent comboItemEvent){
        connectToClient.send(new Message("ComboItemEvent", comboItemEvent));
    }
    public void sendBuyItemEvent(BuyItemEvent buyItemEvent){
        connectToClient.send(new Message("BuyItemEvent", buyItemEvent));
    }
    public void sendMessageEvent(Chat chat){
        connectToClient.send(new Message("MessageEvent", new MessageEvent(chat)));
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

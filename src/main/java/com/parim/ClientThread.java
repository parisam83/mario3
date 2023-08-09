package com.parim;

import com.parim.event.*;
import com.parim.event.chat.block.BlockUserEvent;
import com.parim.event.chat.block.UnblockUserEvent;
import com.parim.event.notification.NotificationEvent;
import com.parim.event.notification.UserNotifications;
import com.parim.event.room.RoomEvent;
import com.parim.model.Chat;
import com.parim.model.User;

import java.net.Socket;
import java.util.ArrayList;

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
            if (title.equals("SendMessageEvent")) server.receivedSendMessageEvent((SendMessageEvent) clientRespond.getFormEvent());
            if (title.equals("ListOfBlockedUsernamesEvent")) sendListOfBlockedUsernames();
            if (title.equals("BlockUserEvent")) server.blockUser((BlockUserEvent) clientRespond.getFormEvent(), this);
            if (title.equals("UnblockUserEvent")) server.unblockUser((UnblockUserEvent) clientRespond.getFormEvent(), this);
            if (title.equals("NewRoomEvent")) server.createNewRoom((RoomEvent) clientRespond.getFormEvent(), this);
            if (title.equals("JoinRoomEvent")) server.joinRoom((RoomEvent) clientRespond.getFormEvent(), this);
            if (title.equals("InviteToRoom")) server.inviteToRoom((RoomEvent) clientRespond.getFormEvent(), this);
            if (title.equals("RemoveFromRoom")) server.removeFromRoom((RoomEvent) clientRespond.getFormEvent(), this);
            if (title.equals("UserNotifications")) sendUserNotifications();
            if (title.equals("AddNewAssistant")) server.addNewAssistant((RoomEvent) clientRespond.getFormEvent());
            if (title.equals("RunRoomGame")) server.runRoomGame((RoomEvent) clientRespond.getFormEvent());
            if (title.equals("VerdictRunRoom")) server.verdictRunRoom((RoomEvent) clientRespond.getFormEvent());
        }
        server.receivedClientClosedEvent(this);
    }

    private void sendUserNotifications() {
        connectToClient.send(new Message("UserNotifications", new UserNotifications(user.getNotifications())));
    }

    private void sendListOfBlockedUsernames(){
        connectToClient.send(new Message("ListOfBlockedUsernamesEvent", new ListOfBlockedUsernamesEvent(user.getBlockedUsernames())));
    }
    public void sendChatListRequest(ArrayList<String> chatList){
        System.out.println(chatList);
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
        System.out.println("server of user " + user.getUsername() + " send " + chat.getMessages());
        connectToClient.send(new Message("MessageEvent", new MessageEvent(chat)));
    }
    public void sendRoomEvent(RoomEvent roomEvent){
        connectToClient.send(new Message("RoomEvent", roomEvent));
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void sendRemoveFromRoom(RoomEvent roomEvent) {
        connectToClient.send(new Message("RemoveFromRoom", roomEvent));
        sendNotificationEvent(roomEvent.getSomeUser(), "You got removed from room " + roomEvent.getRoom().getId(), "Room");

    }
    public void sendNotificationEvent(String title, String message, String type){
        user.addNotification(new NotificationEvent(title, message, type));
        connectToClient.send(new Message("NotificationEvent", new NotificationEvent(title, message, type)));
    }

    public void sendRunRoomGame(RoomEvent roomEvent) {
        connectToClient.send(new Message("RunRoomGame", roomEvent));
    }

    public void sendStartRoomGame(RoomEvent roomEvent) {
        connectToClient.send(new Message("StartRoomGame", roomEvent));
    }
}

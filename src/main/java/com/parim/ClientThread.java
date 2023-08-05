package com.parim;

import com.parim.event.ItemEvent;
import com.parim.event.Message;
import com.parim.event.UserEvent;
import com.parim.model.ItemOfClient;
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
            String title = clientRespond.getTitle();
            if (title.equals("ClientClosedEvent")) server.receivedClientClosedEvent(this);
            if (title.equals("UserRegisterEvent")) server.receivedUserRegisterEvent((UserEvent) clientRespond.getFormEvent(), this);
            if (title.equals("UserLoginEvent")) server.receivedUserLoginEvent((UserEvent) clientRespond.getFormEvent(), this);
            if (title.equals("ItemEvent")) receivedItemEvent();
        }
    }

    private void receivedItemEvent(){
        ItemEvent itemEvent = user.GetItems();
        ArrayList<ItemOfClient> availableItems = itemEvent.getAvailableItems();
        connectToClient.send(new Message("ItemEvent", itemEvent));
        if (availableItems.size() > 1){
            ItemEvent comboItemEvent = new ItemEvent(new ArrayList<ItemOfClient>(){{
                add(availableItems.get(0));
                add(availableItems.get(1));
            }}, null);
            connectToClient.send(new Message("ComboItemEvent", comboItemEvent));
        }
    }
    public void sendRegisterResult(String result) {
        connectToClient.send(new Message(result, null));
    }
    public void sendLoginResult(String result) {
        connectToClient.send(new Message(result, null));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

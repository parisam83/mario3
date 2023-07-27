package com.parim;

import com.parim.event.Message;

import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
    private Server server;
    private ConnectToClient connectToClient;
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
        }
    }
}

package com.parim;

import com.parim.access.UserAccess;
import com.parim.event.UserEvent;
import com.parim.model.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private UserAccess userAccess;
    private ArrayList<ClientThread> openClientThreads = new ArrayList<>();
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
        String username = userEvent.getUsername(), password = userEvent.getPassword();
        if (userAccess.findUser(username, password) == null) {
            userAccess.add(username, password);
            clientThread.sendRegisterResult("UserRegisterSuccessful");
        }
        else clientThread.sendRegisterResult("UserRegisterUnsuccessful");
    }
    public void receivedUserLoginEvent(UserEvent userEvent, ClientThread clientThread) {
        String username = userEvent.getUsername(), password = userEvent.getPassword();
        if (userAccess.findUser(username, password) != null) clientThread.sendLoginResult("UserLoginSuccessful");
        else clientThread.sendLoginResult("UserLoginUnsuccessful");
    }
}
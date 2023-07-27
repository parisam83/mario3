package com.parim;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket;
    private ArrayList<ClientThread> openClientThreads;
    public Server() throws IOException {
        serverSocket = new ServerSocket(9000);
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
}
package com.parim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parim.event.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConnectToClient {
    private Socket socket;
    private ObjectMapper mapper = new ObjectMapper();
    private PrintWriter output;
    private Scanner input;

    public ConnectToClient(Socket socket){
        this.socket = socket;
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Message message){
        try {
            output.println(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public Message receive(){
        String nextLine;
        try {
            try {
                nextLine = input.nextLine();
            } catch (NoSuchElementException e){
                return null;
            }
            return mapper.readValue(nextLine, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}

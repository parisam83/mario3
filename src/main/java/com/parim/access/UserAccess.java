package com.parim.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.parim.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UserAccess {
    private static UserAccess instance;
    ObjectMapper mapper;
    private final String directory = "database/";
    private final File databaseFile = new File(directory);
    private ArrayList<User> users = new ArrayList<>();

    public UserAccess(){
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void add(User user){
        int id = getId(user);
        try {
            mapper.writeValue(new FileWriter(directory + "user" + String.valueOf(id) + ".json"), user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void read(){
        users.clear();
        for (int i = 1; i <= numberOfUsers(); i++) {
            try {
                File file1 = new File(directory + "user" + String.valueOf(i) + ".json");
                User newUser = mapper.readValue(file1, User.class);
                users.add(newUser);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getId(User wantingUser){
        read();
        String username = wantingUser.getUsername(), password = wantingUser.getPassword();
        for (int i = 0; i < users.size(); i++){
            User user = users.get(i);
            if (user.getUsername().equals(username) && user.getPassword().equals(password))
                return i + 1;
        }
        return numberOfUsers() + 1;
    }
    private int numberOfUsers(){
        read();
        if (databaseFile.list() == null) return 0;
        return databaseFile.list().length;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public static UserAccess getInstance() {
        if (instance == null) instance = new UserAccess();
        return instance;
    }
}

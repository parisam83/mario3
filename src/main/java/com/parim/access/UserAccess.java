package com.parim.access;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.parim.Server;
import com.parim.model.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UserAccess {
    ObjectMapper mapper;
    private final String directory = "database/";
    private final File databaseFile = new File(directory);
    private ArrayList<User> users = new ArrayList<>();

    public UserAccess(){
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
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
    public User add(String username, String password){
        User user = new User(username, password);
        users.add(user);
        try {
            mapper.writeValue(new FileWriter(directory + "user" + String.valueOf(numberOfUsers() + 1) + ".json"), user);
            return user;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public User findUser(String username, String password){
        read();
        for (User user : users)
            if (user.getUsername().equals(username) && user.getPassword().equals(password))
                return user;
        return null;
    }
    public int findUserId(String username, String password){
        read();
        for (User user : users)
            if (user.getUsername().equals(username) && user.getPassword().equals(password))
                return users.indexOf(user);
        throw new RuntimeException();
    }
    public void update(User user){
        int index = findUserId(user.getUsername(), user.getPassword()) + 1;
        try {
            mapper.writeValue(new FileWriter(directory + "user" + String.valueOf(index) + ".json"), user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean usernameExists(String username){
        read();
        for (User user : users)
            if (user.getUsername().equals(username))
                return true;
        return false;
    }
    public User findUserByUsername(String username){
        read();
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public int numberOfUsers(){
        if (databaseFile.list() == null)
            return 0;
        return databaseFile.list().length;
    }
    public ArrayList<User> getUsers() {
        read();
        return users;
    }
}
package com.parim.model;

import java.util.ArrayList;

public class Room {
    private String id, password, boss;
    private ArrayList<String> assistants = new ArrayList<>(), blockedUsers = new ArrayList<>(), members = new ArrayList<>();
    private ArrayList<Boolean> startGame = new ArrayList<>();

    public Room() {}

    public void addAssistant(String assistant){
        assistants.add(assistant);
    }
    public void addBlockedUser(String blockedUser){
        blockedUsers.add(blockedUser);
    }
    public void addMember(String member){
        members.add(member);
    }
    public void removeAssistant(String assistant){
        assistants.remove(assistant);
    }
    public void removeMember(String member){
        members.remove(member);
    }
    public void addYes(){
        startGame.add(true);
    }
    public void resetGame(){
        startGame.clear();
    }
    public boolean readyToStart(){
        return GetAllPeople().size() == startGame.size();
    }
    public ArrayList<String> GetAllPeople(){
        ArrayList<String> ans = new ArrayList<>();
        if (members != null && !members.isEmpty()) ans.addAll(members);
        if (assistants != null && !assistants.isEmpty()) ans.addAll(assistants);
        ans.add(boss);
        return ans;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBoss() {
        return boss;
    }

    public void setBoss(String boss) {
        this.boss = boss;
    }

    public ArrayList<String> getAssistants() {
        return assistants;
    }

    public void setAssistants(ArrayList<String> assistants) {
        this.assistants = assistants;
    }

    public ArrayList<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(ArrayList<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
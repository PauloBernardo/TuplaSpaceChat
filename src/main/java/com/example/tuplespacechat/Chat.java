package com.example.tuplespacechat;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    private String name;
    private final ArrayList<String> participants;
    private final ArrayList<ChatMessage> messages;

    public Chat(String name) {
        this.name = name;
        participants = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }
}

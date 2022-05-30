package com.example.tuplespacechat;

import java.io.Serializable;
import java.util.ArrayList;

public class PChat implements Serializable {
    private final ArrayList<ChatMessage> messages;

    public PChat() {
        messages = new ArrayList<>();
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }
}

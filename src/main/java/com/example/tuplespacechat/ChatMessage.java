package com.example.tuplespacechat;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String message;
    private String sender;
    private String client;
    private Long timestamp;

    public ChatMessage(String clientID, String message, long currentTimeMillis) {
        this.message = message;
        this.client = clientID;
        this.timestamp = currentTimeMillis;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

package com.example.tuplespacechat;

import com.example.tuplespacechat.Templates.Chats.ChatMessageTemplate;

import java.rmi.Remote;

public interface MediatorInterface extends Remote {

    boolean sendMessage(ChatMessageTemplate message) throws Exception;

}

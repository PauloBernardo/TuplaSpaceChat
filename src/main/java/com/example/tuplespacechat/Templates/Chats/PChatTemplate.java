package com.example.tuplespacechat.Templates.Chats;

import com.example.tuplespacechat.PChat;
import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;


public class PChatTemplate implements Entry {
    public TOPIC name = TOPIC.PRIVATE_CHAT_TOPIC;
    public String chatOriginName;
    public String client;
    public PChat chat;

    public PChatTemplate(){}
}

package com.example.tuplespacechat.Templates.Chats;

import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;

public class PChatToken implements Entry {
    public TOPIC name = TOPIC.PRIVATE_CHAT_TOPIC;
    public String client;

    public PChatToken(){}
}

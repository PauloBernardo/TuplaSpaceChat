package com.example.tuplespacechat.Templates.Chats;

import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;

public class ChatCount implements Entry {
    public TOPIC name = TOPIC.CHAT_TOPIC;
    public Integer numberOfChats;

    public ChatCount(){}
}

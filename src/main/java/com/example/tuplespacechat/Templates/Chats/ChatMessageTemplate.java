package com.example.tuplespacechat.Templates.Chats;

import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;

public class ChatMessageTemplate implements Entry {
    public TOPIC name = TOPIC.MESSAGE_TOPIC;
    public TOPIC type;
    public String chatName;
    public String message;
    public String sender;
    public String client;

    public ChatMessageTemplate(){}
}

package com.example.tuplespacechat.Templates.Chats;

import com.example.tuplespacechat.Chat;
import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;

public class ChatTemplate implements Entry {
    public TOPIC name = TOPIC.CHAT_TOPIC;
    public String chatName;
    public Chat chat;

    public ChatTemplate(){}
}

package com.example.tuplespacechat.Templates.Clients;

import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;

public class Client implements Entry {
    public TOPIC name = TOPIC.CLIENT_TOPIC;
    public String clientName;

    public Client(){}
}

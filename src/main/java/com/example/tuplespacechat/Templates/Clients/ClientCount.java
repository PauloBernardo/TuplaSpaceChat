package com.example.tuplespacechat.Templates.Clients;

import com.example.tuplespacechat.Templates.TOPIC;
import net.jini.core.entry.Entry;

public class ClientCount implements Entry {
    public TOPIC name = TOPIC.CLIENT_TOPIC;
    public Integer numberOfClients;

    public ClientCount(){}
}

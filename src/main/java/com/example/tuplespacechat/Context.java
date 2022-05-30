package com.example.tuplespacechat;

import com.example.tuplespacechat.Templates.*;
import com.example.tuplespacechat.Templates.Clients.Client;
import com.example.tuplespacechat.Templates.Clients.ClientCount;
import com.example.tuplespacechat.Utils.TIMEOUTS;
import net.jini.space.JavaSpace;

import java.util.ArrayList;

public class Context {
    private static Context instance = null;
    static String clientID;
    private ArrayList<ChatMessage> clientMessages;
    private Chat actualChat;
    private Integer allNumberOfChats;
    static String url;
    private final JavaSpace space;

    private Context() throws Exception {
        super();
        allNumberOfChats = 0;
        clientMessages = new ArrayList<>();
        System.out.println("Procurando pelo servico JavaSpace...");
        Lookup finder = new Lookup(JavaSpace.class);
        this.space = (JavaSpace) finder.getService();
        if (space == null) {
            System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            System.exit(-1);
        }
        System.out.println("O servico JavaSpace foi encontrado.");
        System.out.println(space);
        this.registerClient();
    }

    private void registerClient() throws Exception {
        TopicsCreated template = new TopicsCreated();
        template.topicName = TOPIC.CLIENT_TOPIC;
        TopicsCreated topicsCreated = (TopicsCreated) space.readIfExists(template, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (topicsCreated == null) {
            System.out.println("TOPIC NOT CREATED YET");
            space.write(template, null, TIMEOUTS.PERMANENT.getValue());
            Token token = new Token();
            token.topic = TOPIC.CLIENT_TOPIC;
            Client client = new Client();
            client.clientName = Context.clientID;
            ClientCount clientCount = new ClientCount();
            clientCount.numberOfClients = 1;

            space.write(client, null, TIMEOUTS.PERMANENT.getValue());
            space.write(clientCount, null, TIMEOUTS.PERMANENT.getValue());
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
        } else {
            Token token = new Token();
            token.topic = TOPIC.CLIENT_TOPIC;
            while (true) {
                Token response = (Token) space.take(token, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (response != null) {
                    Client client = new Client();
                    client.clientName = Context.clientID;

                    Client oldUser = (Client) space.read(client, null, TIMEOUTS.QUICK_CHECK.getValue());
                    if (oldUser != null) {
                        space.write(response, null,  TIMEOUTS.PERMANENT.getValue());
                        throw new Exception("Client already exist!");
                    }

                    space.write(client, null, TIMEOUTS.PERMANENT.getValue());
                    ClientCount clientCountTemplate = new ClientCount();
                    ClientCount clientCount = (ClientCount) space.take(clientCountTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                    clientCount.numberOfClients++;
                    if (clientCount.numberOfClients.compareTo(0) <= 0) {
                        clientCount.numberOfClients = 1;
                    }
                    space.write(clientCount, null, TIMEOUTS.PERMANENT.getValue());
                    space.write(response, null,  TIMEOUTS.PERMANENT.getValue());
                    break;
                }
                space.write(token, null, TIMEOUTS.PERMANENT.getValue());
                System.out.println("NÃO HÁ TOKEN DO TÓPICO DE CLIENTE!");
            }
        }
    }

    public static Context getInstance() throws Exception {
        if (instance == null) {
            try {
                instance = new Context();
            } catch (Exception e) {
                e.printStackTrace();
                instance = null;
                throw  e;
            }
        }
        return instance;
    }

    public JavaSpace getSpace() {
        return this.space;
    }

    public void desconnect() throws Exception {
        if (Context.clientID == null) return;
        Token token = new Token();
        token.topic = TOPIC.CLIENT_TOPIC;
        while (true) {
            Token response = (Token) space.take(token, null, TIMEOUTS.QUICK_CHECK.getValue());
            if (response != null) {
                Client client = new Client();
                client.clientName = Context.clientID;
                space.take(client, null,TIMEOUTS.QUICK_CHECK.getValue());
                ClientCount clientCountTemplate = new ClientCount();
                ClientCount clientCount = (ClientCount) space.take(clientCountTemplate, null,TIMEOUTS.QUICK_CHECK.getValue());
                clientCount.numberOfClients--;
                space.write(clientCount, null, TIMEOUTS.PERMANENT.getValue());
                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                break;
            }
            System.out.println("NÃO HÁ TOKEN DO TÓPICO DE CLIENTE!");
        }
        if (this.actualChat != null) {
            ChatManager.leftTheChatRoom();
        }
    }

    public Chat getActualChat() {
        return actualChat;
    }

    public void setActualChat(Chat actualChat) {
        this.actualChat = actualChat;
    }

    public Integer getAllNumberOfChats() {
        return allNumberOfChats;
    }

    public void setAllNumberOfChats(Integer allNumberOfChats) {
        this.allNumberOfChats = allNumberOfChats;
    }

    public ArrayList<ChatMessage> getClientMessages() {
        return clientMessages;
    }

    public void setClientMessages(ArrayList<ChatMessage> clientMessages) {
        this.clientMessages = clientMessages;
    }
}

package com.example.tuplespacechat;

import com.example.tuplespacechat.Templates.Chats.*;
import com.example.tuplespacechat.Templates.TOPIC;
import com.example.tuplespacechat.Templates.Token;
import com.example.tuplespacechat.Templates.TopicsCreated;
import com.example.tuplespacechat.Utils.TIMEOUTS;
import net.jini.space.JavaSpace;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Spy {
    private final MediatorInterface server;
    private final ArrayList<String> suspectWords;

    public Spy(String serverUrl) throws MalformedURLException, NotBoundException, RemoteException {
        server = (MediatorInterface) Naming.lookup(serverUrl + "chat-mediator");
        suspectWords = new ArrayList<>();
    }

    public Spy(String serverUrl, ArrayList<String> supectWords) throws MalformedURLException, NotBoundException, RemoteException {
        server = (MediatorInterface) Naming.lookup(serverUrl + "chat-mediator");
        this.suspectWords = supectWords;
    }

    public static void main(String[] args) throws Exception {
        String serverUrl = args.length > 0 ? args[0] : "rmi://localhost:5431/";
        new Spy(serverUrl).spy();
    }

    public void spy() throws Exception {
        Lookup finder = new Lookup(JavaSpace.class);
        JavaSpace space = (JavaSpace) finder.getService();

        if (space == null) {
            System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            System.exit(-1);
        }

        TopicsCreated template = new TopicsCreated();
        template.topicName = TOPIC.SPY_TOPIC;
        TopicsCreated topicsCreated = (TopicsCreated) space.readIfExists(template, null, TIMEOUTS.QUICK_CHECK.getValue());

        if (topicsCreated == null) {
            System.out.println("TOPIC SPY NOT CREATED YET");
            space.write(template, null, TIMEOUTS.PERMANENT.getValue());
        }

        while (true) {
            ChatMessageTemplate messageTemplate = new ChatMessageTemplate();
            ChatMessageTemplate chatMessageTemplate = (ChatMessageTemplate) space.take(messageTemplate, null, TIMEOUTS.PERMANENT.getValue());
            System.out.println("Mesagem interceptada: " + chatMessageTemplate.message);
            ArrayList<String> suspectWordsFound = new ArrayList<>();

            for(String word: this.suspectWords) {
                if (chatMessageTemplate.message.toLowerCase().trim().contains(word.toLowerCase().trim())) {
                    suspectWordsFound.add(word);
                }
            }

            if (suspectWordsFound.size() > 0) {
                server.sendMessage(chatMessageTemplate);
            }

            if (chatMessageTemplate.type == TOPIC.PRIVATE_CHAT_TOPIC) {
                this.sendPrivateMessage(chatMessageTemplate, space, false);
            } else if (chatMessageTemplate.type == TOPIC.CHAT_TOPIC) {
                this.sendMessage(chatMessageTemplate, space);
            }

        }
    }

    public void sendPrivateMessage(ChatMessageTemplate chatMessageTemplate, JavaSpace space, boolean secondTime) throws Exception {
        PChatCreated template = new PChatCreated();
        template.chatName = chatMessageTemplate.chatName;
        template.client = chatMessageTemplate.client;
        PChatCreated privateChatCreated1 = (PChatCreated) space.readIfExists(template, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (privateChatCreated1 == null) {
            throw new Exception("Não há chat aberto!");
        }

        PChatToken token = new PChatToken();
        token.client = chatMessageTemplate.client;
        PChatToken response = (PChatToken) space.take(token, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (response != null) {
            try {
                PChatTemplate chatTemplate = new PChatTemplate();
                chatTemplate.chatOriginName = chatMessageTemplate.chatName;
                chatTemplate.client = chatMessageTemplate.client;

                PChatTemplate chatT = (PChatTemplate) space.take(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (chatT != null) {
                    ChatMessage chatMessage;
                    if (secondTime) {
                        chatMessage = new ChatMessage(chatMessageTemplate.client, chatMessageTemplate.message, System.currentTimeMillis());
                        chatMessage.setSender(chatMessageTemplate.sender);
                    } else {
                        chatMessage = new ChatMessage(chatMessageTemplate.sender, chatMessageTemplate.message, System.currentTimeMillis());
                        chatMessage.setSender(null);
                    }
                    chatT.chat.getMessages().add(chatMessage);
                    space.write(chatT, null, TIMEOUTS.PERMANENT.getValue());
                    space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                    if (chatMessageTemplate.sender != null && !secondTime) {
                        String aux;
                        aux = "" + chatMessageTemplate.client;
                        chatMessageTemplate.client = chatMessageTemplate.sender;
                        chatMessageTemplate.sender = aux;
                        this.sendPrivateMessage(chatMessageTemplate, space, true);
                    }
                } else {
                    throw new Exception("Chat doesn't exist!");
                }
            } catch (Exception e) {
                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                throw e;
            }
        } else {
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
            throw new Exception("NÃO HÁ TOKEN DO TÓPICO DE CHAT PRIVADO!");
        }
    }

    public void sendMessage(ChatMessageTemplate chatMessageTemplate, JavaSpace space) throws Exception {
        Token token = new Token();
        token.topic = TOPIC.CHAT_TOPIC;
        Token response = (Token) space.take(token, null, TIMEOUTS.CHECK.getValue());
        if (response != null) {
            try {
                ChatTemplate chatTemplate = new ChatTemplate();
                chatTemplate.chatName = chatMessageTemplate.chatName;

                ChatTemplate chatT = (ChatTemplate) space.take(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (chatT != null) {
                    Chat chat = chatT.chat;
                    chat.getMessages().add(new ChatMessage(chatMessageTemplate.client, chatMessageTemplate.message, System.currentTimeMillis()));
                    space.write(chatT, null, TIMEOUTS.PERMANENT.getValue());
                    space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                } else {
                    throw new Exception("Chat doesn't exist!");
                }
            } catch (Exception e) {
                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                throw new Exception("Chat doesn't exist!");
            }
        } else {
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
            throw new Exception("NÃO HÁ TOKEN DO TÓPICO DE CHAT!");
        }
    }
}

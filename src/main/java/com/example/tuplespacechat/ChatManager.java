package com.example.tuplespacechat;

import com.example.tuplespacechat.Templates.Chats.*;
import com.example.tuplespacechat.Templates.Clients.ClientCount;
import com.example.tuplespacechat.Templates.TOPIC;
import com.example.tuplespacechat.Templates.Token;
import com.example.tuplespacechat.Templates.TopicsCreated;
import com.example.tuplespacechat.Utils.TIMEOUTS;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ChatManager {

    static int getNumberOfClients() {
        try {
            Context context = Context.getInstance();
            JavaSpace space = context.getSpace();
            ClientCount template = new ClientCount();
            ClientCount response = (ClientCount) space.read(template, null, TIMEOUTS.QUICK_CHECK.getValue());
            if (response == null) {
                return -1;
            }
            return response.numberOfClients;
        } catch (Exception e) {

            return -1;
        }
    }

    static void createChatRoom(String roomName) throws Exception {
        if (roomName == null || roomName.equals("")) {
            throw new Exception("Nome da Sala nulo!");
        }
        Context context = Context.getInstance();
        JavaSpace space = context.getSpace();

        Token token = new Token();
        token.topic = TOPIC.CHAT_TOPIC;

        TopicsCreated template = new TopicsCreated();
        template.topicName = TOPIC.CHAT_TOPIC;
        TopicsCreated topicsCreated = (TopicsCreated) space.readIfExists(template, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (topicsCreated == null) {
            space.write(template, null, TIMEOUTS.PERMANENT.getValue());
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
        }

        Token response = (Token) space.take(token, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (response != null) {
            try {
                ChatTemplate chatTemplate = new ChatTemplate();
                chatTemplate.chatName = roomName;

                ChatTemplate oldUser = (ChatTemplate) space.read(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (oldUser != null) {
                    throw new Exception("Chat already exist!");
                }

                Chat chat = new Chat(roomName);
                chat.getParticipants().add(Context.clientID);
                chatTemplate.chat = chat;
                space.write(chatTemplate, null, TIMEOUTS.PERMANENT.getValue());

                ChatCount chatCountTemplate = new ChatCount();
                ChatCount chatCount = (ChatCount) space.take(chatCountTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (chatCount == null) {
                    chatCount = new ChatCount();
                    chatCount.numberOfChats = 0;
                }
                chatCount.numberOfChats++;
                if (chatCount.numberOfChats < 0) {
                    chatCount.numberOfChats = 1;
                }
                space.write(chatCount, null, TIMEOUTS.PERMANENT.getValue());
                context.setActualChat(chat);

                // Create CHAT CLIENT
                createPrivateChat(roomName, context, space);

                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
            } catch (Exception e) {
                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                throw e;
            }
        } else {
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
            throw new Exception("NÃO HÁ TOKEN DO TÓPICO DE CHAT!");
        }
    }

    static void joinTheChatRoom(String roomName) throws Exception {
        if (roomName == null || roomName.equals("")) {
            throw new Exception("Messagem é nula!");
        }

        Context context = Context.getInstance();

        JavaSpace space = context.getSpace();

        Token token = new Token();
        token.topic = TOPIC.CHAT_TOPIC;
        Token response = (Token) space.take(token, null, TIMEOUTS.CHECK.getValue());
        if (response != null) {
            try {
                ChatTemplate chatTemplate = new ChatTemplate();
                chatTemplate.chatName = roomName;

                ChatTemplate chatT = (ChatTemplate) space.take(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (chatT != null) {
                    Chat chat = chatT.chat;
                    chat.getParticipants().add(Context.clientID);
                    context.setActualChat(chat);
                    space.write(chatT, null, TIMEOUTS.PERMANENT.getValue());

                    // Create CHAT CLIENT
                    PChatCreated templatePchat = new PChatCreated();
                    templatePchat.chatName = roomName;
                    templatePchat.client = Context.clientID;
                    PChatCreated res = (PChatCreated) space.readIfExists(templatePchat, null, TIMEOUTS.QUICK_CHECK.getValue());

                    if (res == null) {
                        createPrivateChat(roomName, context, space);
                    }

                    space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                } else {
                    throw new Exception("Chat doesn't exist!");
                }
            } catch (Exception e) {
                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                throw e;
            }
        } else {
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
            throw new Exception("NÃO HÁ TOKEN DO TÓPICO DE CHAT!");
        }
    }

    private static void createPrivateChat(String roomName, Context context, JavaSpace space) throws TransactionException, RemoteException {
        PChatTemplate chatClient = new PChatTemplate();
        chatClient.client = Context.clientID;
        chatClient.chatOriginName = roomName;
        chatClient.chat = new PChat();
        space.write(chatClient, null, TIMEOUTS.PERMANENT.getValue());

        PChatCreated privateChatCreated = new PChatCreated();
        privateChatCreated.chatName = roomName;
        privateChatCreated.client = Context.clientID;
        space.write(privateChatCreated, null, TIMEOUTS.PERMANENT.getValue());

        PChatToken privateChatToken = new PChatToken();
        privateChatToken.client = Context.clientID;
        space.write(privateChatToken, null, TIMEOUTS.PERMANENT.getValue());
        context.setClientMessages(new ArrayList<>());
    }

    static void leftTheChatRoom() throws Exception {
        Context context = Context.getInstance();
        if (context.getActualChat() == null) {
            throw new Exception("Não há chat aberto!");
        }

        JavaSpace space = context.getSpace();

        Token token = new Token();
        token.topic = TOPIC.CHAT_TOPIC;
        Token response = (Token) space.take(token, null, TIMEOUTS.CHECK.getValue());
        if (response != null) {
            try {
                ChatTemplate chatTemplate = new ChatTemplate();
                chatTemplate.chatName = context.getActualChat().getName();

                ChatTemplate chatT = (ChatTemplate) space.take(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
                if (chatT != null) {
                    Chat chat = chatT.chat;
                    chat.getParticipants().remove(Context.clientID);
                    space.write(chatT, null, TIMEOUTS.PERMANENT.getValue());
                    space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                    context.setActualChat(null);
                    context.setClientMessages(null);
                } else {
                    throw new Exception("Chat doesn't exist!");
                }
            } catch (Exception e) {
                space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                throw e;
            }
        } else {
            space.write(token, null, TIMEOUTS.PERMANENT.getValue());
            throw new Exception("NÃO HÁ TOKEN DO TÓPICO DE CHAT!");
        }
    }

    static void sendMessage(String message) throws Exception {
        if (message == null || message.equals("")) {
            throw new Exception("Messagem é nula!");
        }

        Context context = Context.getInstance();
        if (context.getActualChat() == null) {
            throw new Exception("Não há chat aberto!");
        }

        JavaSpace space = context.getSpace();

        ChatMessageTemplate chatMessageTemplate = new ChatMessageTemplate();
        chatMessageTemplate.client = Context.clientID;
        chatMessageTemplate.chatName = context.getActualChat().getName();
        chatMessageTemplate.message = message;
        chatMessageTemplate.type = TOPIC.CHAT_TOPIC;
        space.write(chatMessageTemplate, null, TIMEOUTS.PERMANENT.getValue());
    }

    static ArrayList<Chat> checkAndGetAllChats(boolean first) throws Exception {
        ArrayList<Chat> chats = null;
        Context context = Context.getInstance();
        JavaSpace space = context.getSpace();
        ChatCount chatCountTemplate = new ChatCount();
        ChatCount chatCount = (ChatCount) space.readIfExists(chatCountTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (chatCount != null) {
            if (first || context.getAllNumberOfChats().compareTo(chatCount.numberOfChats) != 0) {
                chats = new ArrayList<>();
                Token token = new Token();
                token.topic = TOPIC.CHAT_TOPIC;
                Token response = (Token) space.take(token, null, TIMEOUTS.CHECK.getValue());
                if (response != null) {
                    try {
                        ChatTemplate template = new ChatTemplate();
                        ArrayList<ChatTemplate> chatTemplates = new ArrayList<>();
                        while (true) {
                            ChatTemplate chatTemplate = (ChatTemplate) space.take(template, null, TIMEOUTS.QUICK_CHECK.getValue());
                            if (chatTemplate != null) {
                                chatTemplates.add(chatTemplate);
                                chats.add(chatTemplate.chat);
                            } else {
                                break;
                            }
                        }
                        context.setAllNumberOfChats(chatTemplates.size());
                        for (ChatTemplate chatTemplate : chatTemplates) {
                            space.write(chatTemplate, null, TIMEOUTS.PERMANENT.getValue());
                        }
                        space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                    } catch (Exception e) {
                        space.write(response, null, TIMEOUTS.PERMANENT.getValue());
                        throw e;
                    }
                } else {
                    space.write(token, null, TIMEOUTS.PERMANENT.getValue());
                    throw new Exception("NÃO HÁ TOKEN DO TÓPICO DE CHAT!");
                }
            }
        } else {
            System.out.println("NÃO HÁ CHATS!");
        }
        return chats;
    }

    static boolean checkAndUpdateChatFromSpace() throws Exception {
        Context context = Context.getInstance();
        JavaSpace space = context.getSpace();
        Chat actualChat = context.getActualChat();
        ChatTemplate chatTemplate = new ChatTemplate();
        chatTemplate.chatName = actualChat.getName();
        ChatTemplate chatT = (ChatTemplate) space.readIfExists(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (
                chatT == null ||
                        (
                                chatT.chat.getMessages().size() == context.getActualChat().getMessages().size() &&
                                        chatT.chat.getParticipants().size() == context.getActualChat().getParticipants().size()
                        )
        ) {
            return false;
        }
        context.setActualChat(chatT.chat);
        return true;
    }


    static void sendPrivateMessage(String message, String client) throws Exception {
        if (message == null || message.equals("")) {
            throw new Exception("Messagem é nula!");
        }

        Context context = Context.getInstance();
        if (context.getClientMessages() == null) {
            throw new Exception("Não há chat aberto!");
        }

        JavaSpace space = context.getSpace();

        ChatMessageTemplate chatMessageTemplate = new ChatMessageTemplate();
        chatMessageTemplate.client = client;
        chatMessageTemplate.sender = Context.clientID;
        chatMessageTemplate.chatName = context.getActualChat().getName();
        chatMessageTemplate.message = message;
        chatMessageTemplate.type = TOPIC.PRIVATE_CHAT_TOPIC;
        space.write(chatMessageTemplate, null, TIMEOUTS.PERMANENT.getValue());
    }

    static boolean checkAndUpdatePrivateChatFromSpace() throws Exception {
        Context context = Context.getInstance();
        JavaSpace space = context.getSpace();
        Chat actualChat = context.getActualChat();
        PChatTemplate chatTemplate = new PChatTemplate();
        chatTemplate.chatOriginName = actualChat.getName();
        chatTemplate.client = Context.clientID;
        PChatTemplate chatT = (PChatTemplate) space.readIfExists(chatTemplate, null, TIMEOUTS.QUICK_CHECK.getValue());
        if (
                chatT == null ||
                        (
                                chatT.chat.getMessages().size() == context.getClientMessages().size()
                        )
        ) {
            return false;
        }
        context.setClientMessages(chatT.chat.getMessages());
        return true;
    }
}

package com.example.tuplespacechat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.jms.*;

import com.example.tuplespacechat.Templates.Chats.ChatMessageTemplate;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Mediator extends UnicastRemoteObject implements MediatorInterface {
    private final Session session;
    private final Connection connection;
    private final MessageProducer publisher;

    protected Mediator(String url) throws RemoteException, JMSException {
        super();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connectionFactory.setClientID("mediator");
        connectionFactory.setConnectionIDPrefix("mediator");
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination dest = session.createTopic("chatMediator");
        publisher = session.createProducer(dest);
    }

    @Override
    public boolean sendMessage(ChatMessageTemplate message) throws Exception {
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText(message.message);
        publisher.send(textMessage);
        return true;
    }

    public static void main(String[] args) {
        int serverUrl = args.length > 0 ? Integer.parseInt(args[0]) : 5431;
        String url = args.length > 0 ? args[0] : ActiveMQConnection.DEFAULT_BROKER_URL;
        try {
            Mediator server = new Mediator(url);
            Registry registry = LocateRegistry.createRegistry(serverUrl);
            registry.rebind("chat-mediator", server);
            System.out.println("Server started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

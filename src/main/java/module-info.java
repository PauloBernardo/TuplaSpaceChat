module com.example.tuplespacechat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.naming;
    requires jdk.net;
    requires java.rmi;
    requires jini.core;
    requires jini.ext;
    requires activemq.client;
    requires javax.jms;

    opens com.example.tuplespacechat to javafx.fxml;
    exports com.example.tuplespacechat;
    exports com.example.tuplespacechat.Templates;
    opens com.example.tuplespacechat.Templates to javafx.fxml;
    exports com.example.tuplespacechat.Templates.Clients;
    opens com.example.tuplespacechat.Templates.Clients to javafx.fxml;
    exports com.example.tuplespacechat.Templates.Chats;
    opens com.example.tuplespacechat.Templates.Chats to javafx.fxml;
    exports com.example.tuplespacechat.Utils;
    opens com.example.tuplespacechat.Utils to javafx.fxml;
}
package com.example.tuplespacechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChatSpyApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TupleSpaceChatApplication.class.getResource("spy-view.fxml"));
        loader.setResources(ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR")));
        Scene scene = new Scene(loader.load());
        Image image = new Image("file:spy.png");
        stage.getIcons().add(image);
        stage.setTitle("CHAT SPY!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        SpyContext.url = args.length > 0 ? args[0] : "rmi://localhost:5431/";
        launch();
    }
}
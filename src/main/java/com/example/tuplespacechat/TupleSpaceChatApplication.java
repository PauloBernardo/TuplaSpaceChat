package com.example.tuplespacechat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class TupleSpaceChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(TupleSpaceChatApplication.class.getResource("hello-view.fxml"));
        loader.setResources(ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR")));
        Scene scene = new Scene(loader.load());
        Image image = new Image("file:chat.png");
        stage.getIcons().add(image);
        stage.setTitle("TUPLE SPACE CHAT!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        try {
            Context.getInstance().desconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        Context.url = args.length > 0 ? args[0] : "";
        launch();
    }
}
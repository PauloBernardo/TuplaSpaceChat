package com.example.tuplespacechat;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class SpyView {

    @FXML
    public FlowPane wordsContainer;
    @FXML
    public TextField wordField;
    ArrayList<String> suspectWords;
    Spy spy;

    ResourceBundle bundle = ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR"));

    Thread th;

    @FXML
    public void initialize() {
        try {
            suspectWords = new ArrayList<>();
            spyChat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spyChat() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                spy = new Spy(SpyContext.url, suspectWords);
                spy.spy();
                return null;
            }
        };
        th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }


    public void onAddWord() {
        if(wordField.getText() != null && !wordField.getText().trim().equals("")) {
            this.suspectWords.add(wordField.getText());
            wordField.clear();
            th.interrupt();
            spy = null;
            spyChat();
            updateWordsContainer();
        }
    }
    
    private void updateWordsContainer() {
        wordsContainer.getChildren().clear();
        for(String word: suspectWords) {
            //Creating a Label
            Pane pane = new Pane();
            pane.setMinHeight(40);
            pane.setOnMouseClicked(event -> {
                try {
                    this.suspectWords.remove(word);
                    wordField.clear();
                    th.interrupt();
                    spy = null;
                    spyChat();
                    updateWordsContainer();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(bundle.getString("menuPrincipal.createChatError"));
                    alert.getDialogPane().setContent( new Label(bundle.getString("menuPrincipal.createChatErrorText")));
                    alert.show();
                    e.printStackTrace();
                }
            });
            Label label = new Label(word);
            label.setLayoutY(12);
            label.setAlignment(Pos.CENTER);
            //Setting font to the label
            label.getStyleClass().add("namePlayerLabel");
            pane.getStyleClass().add("chatsContainer");
            label.setPadding(new Insets(0, 0, 0, 10));
            pane.getChildren().add(label);
            wordsContainer.getChildren().add(pane);
            Pane space = new Pane();
            space.setPrefWidth(20);
            space.setMinHeight(20);

            wordsContainer.getChildren().add(space);
        }
    }
}

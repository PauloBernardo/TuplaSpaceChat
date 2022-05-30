package com.example.tuplespacechat;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class MenuPrincipal extends ResizableView {
    @FXML
    public Label clientNome;
    @FXML
    public Label labelSpace;
    @FXML
    public Label numberOfClients;
    public TextField chatNameField;
    @FXML
    public FlowPane chatsContainer;

    ResourceBundle bundle = ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR"));

    Timeline updateFields;

    @FXML
    public void initialize() {
        try {
            Context context = Context.getInstance();
            clientNome.setText(Context.clientID);
            labelSpace.setText(context.getSpace().toString().split(" ")[2]);
            numberOfClients.setText(Integer.toString(ChatManager.getNumberOfClients()));
            updateChats(true);
            numberOfClients.setText(Integer.toString(ChatManager.getNumberOfClients()));
            updateFields = new Timeline(
                    new KeyFrame(
                            Duration.seconds(10),
                            event -> {
                                updateChats(false);
                                numberOfClients.setText(Integer.toString(ChatManager.getNumberOfClients()));
                            }
                    )
            );
            updateFields.setCycleCount(Timeline.INDEFINITE);
            updateFields.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateChats(boolean first) {
        try {
            System.out.println("ATUALIZANDO CHATS");
            ArrayList<Chat> chats = ChatManager.checkAndGetAllChats(first);

            if(chats != null) {
                chatsContainer.getChildren().clear();
                for(Chat chat: chats) {
                    //Creating a Label
                    Pane pane = new Pane();
                    pane.setMinHeight(40);
                    pane.setOnMouseClicked(event -> {
                        try {
                            ChatManager.joinTheChatRoom(chat.getName());
                            updateFields.stop();
                            this.switchBetweenScreen(((Node) event.getSource()).getScene(), "chat-view.fxml");
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle(bundle.getString("menuPrincipal.createChatError"));
                            alert.getDialogPane().setContent( new Label(bundle.getString("menuPrincipal.createChatErrorText")));
                            alert.show();
                            e.printStackTrace();
                        }
                    });
                    Label label = new Label(chat.getName());
                    label.setLayoutY(12);
                    label.setAlignment(Pos.CENTER);
                    //Setting font to the label
                    label.getStyleClass().add("namePlayerLabel");
                    pane.getStyleClass().add("chatsContainer");
                    label.setPadding(new Insets(0, 0, 0, 10));
                    pane.getChildren().add(label);
                    chatsContainer.getChildren().add(pane);
                    Pane space = new Pane();
                    space.setPrefWidth(20);
                    space.setMinHeight(20);

                    chatsContainer.getChildren().add(space);
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("menuPrincipal.getChatError"));
            alert.getDialogPane().setContent( new Label(bundle.getString("menuPrincipal.getChatErrorText")));
            alert.show();
            e.printStackTrace();
        }
    }

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        try {
            ChatManager.createChatRoom(chatNameField.getText());
            updateFields.stop();
            this.switchBetweenScreen(((Node) event.getSource()).getScene(), "chat-view.fxml");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("menuPrincipal.createChatError"));
            alert.getDialogPane().setContent( new Label(bundle.getString("menuPrincipal.createChatErrorText")));
            alert.show();
            e.printStackTrace();
        }
    }
}
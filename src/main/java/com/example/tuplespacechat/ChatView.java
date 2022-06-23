package com.example.tuplespacechat;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class ChatView extends ResizableView {

    @FXML
    public TextField messageField;
    @FXML
    public VBox messageBox;
    @FXML
    public VBox participantsBox;
    @FXML
    public Label privateChatLabel;
    @FXML
    public TextField messagePrivateField;
    @FXML
    public VBox privateChatBox;
    @FXML
    public Button sendPrivateButton;
    @FXML
    public Label titleChat;
    @FXML
    public Button sendPrivateButton1;
    private String participantPrivate;
    private Button participantButton;

    private final String[] colors = {
            "black",
            "gray",
            "brown",
            "red",
            "yellow",
            "cyan",
            "violet",
            "silver",
            "tomato"
    };

    private final Map<String, String> participantColor = new HashMap<>();
    private int colorIndex = 1;

    ResourceBundle bundle = ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR"));

    Timeline updateFields;

    @FXML
    public void initialize() {
        try {
            Context context = Context.getInstance();
//            context.addToRead(new Message());
            titleChat.setText(bundle.getString("chat.titleChat") + " " + context.getActualChat().getName());
            participantColor.put(Context.clientID, colors[0]);
            messageField.setOnKeyPressed(keyEvent -> {
                try {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        chatMessageSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            messagePrivateField.setOnKeyPressed(keyEvent -> {
                try {
                    if (keyEvent.getCode() == KeyCode.ENTER) {
                        chatMessagePrivateSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            messagePrivateField.setDisable(true);
            sendPrivateButton.setDisable(true);
            chatUpdate(true);
            privateChatUpdate(true);
            updateFields = new Timeline(
                    new KeyFrame(
                            Duration.seconds(2),
                            event -> {
                                chatUpdate(false);
                                privateChatUpdate(false);
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
    private void chatUpdate(boolean first) {
        try {
            if (first || ChatManager.checkAndUpdateChatFromSpace()) {
                Chat chat = Context.getInstance().getActualChat();
                messageBox.getChildren().clear();
                participantsBox.getChildren().clear();
                for(String participant: chat.getParticipants()) {
                    HBox hBox = new HBox();
                    hBox.setMinWidth(290);
                    hBox.setMaxWidth(290);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    //Creating a Label
                    Label label = new Label(participant);
                    label.setPadding(new Insets(0, 20, 0, 10));
                    label.getStyleClass().add("namePlayerLabel");

                    if (participantColor.get(participant) != null) {
                        label.getStyleClass().add(participantColor.get(participant));
                    } else {
                        participantColor.put(participant, colors[colorIndex]);
                        label.getStyleClass().add(participantColor.get(participant));
                        colorIndex++;
                        if (colorIndex >= colors.length) colorIndex = 0;
                    }
                    Button button = new Button();
                    button.getStyleClass().add("chatButton");
                    button.setText("Chat Privado");
                    if (participant.equals(this.participantPrivate)) {
                        button.setDisable(true);
                        this.participantButton = button;
                    }
                    button.setOnMouseClicked(event -> {
                        this.participantPrivate = participant;
                        this.participantButton = button;
                        privateChatLabel.setText(bundle.getString("chat.privateChatBox") + " " + participant);
                        messagePrivateField.setDisable(false);
                        sendPrivateButton.setDisable(false);
                        privateChatUpdate(true);
                    });
                    //Setting font to the label
                    hBox.setPadding(new Insets(10,0,10,0));
                    hBox.getChildren().add(label);
                    if (participant.equals(Context.clientID)) {
                        label.setText(participant + " " + "(Você)");
                    } else {
                        hBox.getChildren().add(button);
                    }
                    participantsBox.getChildren().add(hBox);
                }
                for(ChatMessage chatMessage: chat.getMessages()) {
                    if (chatMessage.getClient().equals(Context.clientID)) {
                        VBox box = new VBox();
                        HBox.setMargin(box, new Insets(0, 10, 0, 10));
                        box.getStyleClass().add("message");
                        box.setMaxWidth(187);
                        box.setPadding(new Insets(10, 10, 10, 10));
                        //Creating a Label
                        Label label = new Label(Context.clientID);
                        //Setting font to the label
                        label.getStyleClass().add("namePlayerLabel");
                        box.getChildren().add(label);

                        //Creating a Label
                        Label label1 = new Label(chatMessage.getMessage());
                        label1.getStyleClass().add("messagePlayerLabel");
                        //Setting the position
                        label1.setLayoutX(100);
                        label1.setLayoutY(100);
                        label1.setPadding(new Insets(0, 10, 0, 10));
                        label1.setMaxWidth(187);

                        Text text = new Text();
                        text.setText(chatMessage.getMessage());
                        text.wrappingWidthProperty().set(187);

                        box.getChildren().add(text);
                        messageBox.getChildren().add(box);
                        messageBox.getChildren().add(new Label("\n"));
                    } else {
                        HBox hBox = new HBox();
                        hBox.minWidth(287);
                        hBox.maxWidth(287);
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                        hBox.setPadding(new Insets(0, 0, 0, 90));

                        VBox box = new VBox();
                        box.setAlignment(Pos.CENTER_RIGHT);
                        box.setPrefWidth(187);
                        box.getStyleClass().add("message");
                        box.setPadding(new Insets(10, 10, 10, 10));

                        //Creating a Label
                        Label label = new Label(chatMessage.getClient());
                        putClientClor(chatMessage, label);
                        label.setAlignment(Pos.CENTER_RIGHT);
                        //Setting font to the label
                        label.getStyleClass().add("namePlayerLabel");

                        //Creating a Label
                        Label label1 = new Label(chatMessage.getMessage());
                        label1.getStyleClass().add("messageAnotherLabel");
                        label1.setMaxWidth(187);
                        Text text = new Text();
                        text.setText(chatMessage.getMessage());
                        text.wrappingWidthProperty().set(187);
                        VBox.setVgrow(label1, Priority.ALWAYS);
                        label1.setWrapText( true );
                        label1.setMaxHeight( Double.MAX_VALUE );
                        //Setting the position

                        box.getChildren().add(label);
                        box.getChildren().add(label1);
                        hBox.getChildren().add(box);
                        messageBox.getChildren().add(hBox);
                        messageBox.getChildren().add(new Label("\n"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("chat.updateChatError"));
            alert.setContentText(bundle.getString("chat.updateChatErrorText"));
            alert.show();
        }
    }


    @FXML
    private void privateChatUpdate(boolean first) {
        try {
            if (first || ChatManager.checkAndUpdatePrivateChatFromSpace()) {
                ArrayList<ChatMessage> messages = Context.getInstance().getClientMessages();
                privateChatBox.getChildren().clear();
                for(ChatMessage chatMessage: messages) {
                    if (chatMessage.getClient().equals(Context.clientID)) {
                        if (
                                this.participantPrivate != null  &&
                                !chatMessage.getSender().equals(this.participantPrivate)
                        ) continue;
                        VBox box = new VBox();
                        HBox.setMargin(box, new Insets(0, 10, 0, 10));
                        box.getStyleClass().add("message");
                        box.setMaxWidth(187);
                        box.setPadding(new Insets(10, 10, 10, 10));
                        //Creating a Label
                        Label label = new Label(Context.clientID);
                        //Setting font to the label
                        label.getStyleClass().add("namePlayerLabel");
                        box.getChildren().add(label);

                        //Creating a Label
                        Label label1 = new Label(chatMessage.getMessage());
                        label1.getStyleClass().add("messagePlayerLabel");
                        //Setting the position
                        label1.setLayoutX(100);
                        label1.setLayoutY(100);
                        label1.setPadding(new Insets(0, 10, 0, 10));
                        label1.setMaxWidth(187);

                        Text text = new Text();
                        text.setText(chatMessage.getMessage());
                        text.wrappingWidthProperty().set(187);

                        box.getChildren().add(text);
                        privateChatBox.getChildren().add(box);
                        privateChatBox.getChildren().add(new Label("\n"));
                    } else {
                        if (
                                this.participantPrivate != null  &&
                                !chatMessage.getClient().equals(this.participantPrivate)
                        ) continue;
                        HBox hBox = new HBox();
                        hBox.minWidth(287);
                        hBox.maxWidth(287);
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                        hBox.setPadding(new Insets(0, 0, 0, 90));

                        VBox box = new VBox();
                        box.setAlignment(Pos.CENTER_RIGHT);
                        box.setPrefWidth(187);
                        box.getStyleClass().add("message");
                        box.setPadding(new Insets(10, 10, 10, 10));

                        //Creating a Label
                        Label label = new Label(chatMessage.getClient());
                        label.setAlignment(Pos.CENTER_RIGHT);
                        //Setting font to the label
                        label.getStyleClass().add("namePlayerLabel");
                        putClientClor(chatMessage, label);

                        //Creating a Label
                        Label label1 = new Label(chatMessage.getMessage());
                        label1.getStyleClass().add("messageAnotherLabel");
                        label1.setMaxWidth(187);
                        Text text = new Text();
                        text.setText(chatMessage.getMessage());
                        text.wrappingWidthProperty().set(187);
                        VBox.setVgrow(label1, Priority.ALWAYS);
                        label1.setWrapText( true );
                        label1.setMaxHeight( Double.MAX_VALUE );
                        //Setting the position

                        box.getChildren().add(label);
                        box.getChildren().add(label1);
                        hBox.getChildren().add(box);
                        privateChatBox.getChildren().add(hBox);
                        privateChatBox.getChildren().add(new Label("\n"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("chat.updateChatError"));
            alert.setContentText(bundle.getString("chat.updateChatErrorText"));
            alert.show();
        }
    }

    private void putClientClor(ChatMessage chatMessage, Label label) {
        if (participantColor.get(chatMessage.getClient()) != null) {
            label.getStyleClass().add(participantColor.get(chatMessage.getClient()));
        } else {
            participantColor.put(chatMessage.getClient(), colors[colorIndex]);
            label.getStyleClass().add(participantColor.get(chatMessage.getClient()));
            colorIndex++;
            if (colorIndex >= colors.length) colorIndex = 0;
        }
    }

    @FXML
    private void chatMessageSend() {
        try {
            ChatManager.sendMessage(messageField.getText());
            messageField.clear();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("chat.sendMessageTitleError"));
            alert.setContentText(bundle.getString("chat.sendMessageTextError"));
            alert.show();
        }
    }

    @FXML
    public void onCloseAction(ActionEvent event) throws Exception {
        ChatManager.leftTheChatRoom();
        updateFields.stop();
        this.switchBetweenScreen(((Node) event.getSource()).getScene(), "menu-view.fxml");
    }

    public void chatMessagePrivateSend() {
        try {
            ChatManager.sendPrivateMessage(messagePrivateField.getText(), this.participantPrivate);
            messagePrivateField.clear();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("chat.sendMessageTitleError"));
            alert.setContentText(bundle.getString("chat.sendMessageTextError"));
            alert.show();
        }
    }

    public void allPrivateChat() {
        this.participantButton.setDisable(false);
        this.participantPrivate = null;
        privateChatLabel.setText(bundle.getString("chat.privateChatBox"));
        messagePrivateField.setDisable(true);
        sendPrivateButton.setDisable(true);
        privateChatUpdate(true);
    }
}

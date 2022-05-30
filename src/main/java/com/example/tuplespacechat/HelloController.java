package com.example.tuplespacechat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Locale;
import java.util.ResourceBundle;

public class HelloController extends ResizableView {
    @FXML
    public TextField idField;

    ResourceBundle bundle = ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR"));

    @FXML
    public void initialize() {}

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        try {
            if(idField.getText() == null || idField.getText().equals("")) {
                throw new Exception("Campo nome vazio!");
            }
            Context.clientID = idField.getText();
            Context.getInstance();
            this.switchBetweenScreen(((Node) event.getSource()).getScene(), "menu-view.fxml");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(bundle.getString("hello.nomeTitleError"));
            alert.getDialogPane().setContent( new Label(bundle.getString("hello.nomeTextError")));
            alert.show();
        }
    }
}
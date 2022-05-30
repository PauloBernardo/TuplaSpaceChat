package com.example.tuplespacechat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ResizableView {
    protected Stage stage;

    @FXML
    public AnchorPane root;

    @FXML
    public void setStage(Stage stage) {
        this.stage = stage;
        root.setPrefWidth(stage.getScene().getWidth());
        root.setPrefHeight(stage.getScene().getHeight());
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);

        stage.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> root.setPrefWidth(newSceneWidth.doubleValue()));
        stage.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> root.setPrefWidth(newSceneHeight.doubleValue()));
    }

    public void switchBetweenScreen(Scene oldScene, String newScreen) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(newScreen)));
        fxmlLoader.setResources(ResourceBundle.getBundle("com.example.tuplespacechat.i18n", new Locale("pt_br", "pt_BR")));
        Parent rootMain = fxmlLoader.load();
        ResizableView controller = fxmlLoader.getController();
        Stage stage = (Stage) oldScene.getWindow();
        controller.setStage(stage);
        Scene scene = new Scene(rootMain);
        stage.setScene(scene);
        stage.show();
    }

}

package com.j_ssh.controller;

import com.j_ssh.api.API;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;

public class SessionController {
    public void changeStage(Stage primaryStage) {
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        VBox vBox = new VBox();
        API.get().createToolbox(vBox);
        Scene scene = new Scene(vBox);
    }
}

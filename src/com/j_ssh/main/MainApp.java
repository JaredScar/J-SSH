package com.j_ssh.main;

import com.j_ssh.controller.TerminalController;
import com.j_ssh.model.Connection;
import com.j_ssh.model.TerminalTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class MainApp extends Application {
    private TerminalController terminalTroller = new TerminalController();
    @Override
    public void start(Stage primaryStage) {
        // This is the primary stage for the app starting
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

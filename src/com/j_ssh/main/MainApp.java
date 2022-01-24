package com.j_ssh.main;

import com.j_ssh.controller.TerminalController;
import com.j_ssh.model.Connection;
import com.j_ssh.model.TerminalTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;

public class MainApp extends Application {
    private static MainApp main;
    private Stage primaryStage;
    private TerminalController terminalTroller;

    public static MainApp get() {
        return main;
    }

    @Override
    public void start(Stage primaryStage) {
        // This is the primary stage for the app starting
        this.primaryStage = primaryStage;
        main = this;
        this.terminalTroller = new TerminalController();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

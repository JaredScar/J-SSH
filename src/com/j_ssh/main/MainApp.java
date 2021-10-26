package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    private DashboardController dashController = new DashboardController();

    @Override
    public void start(Stage primaryStage) {
        this.dashController.changeStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
